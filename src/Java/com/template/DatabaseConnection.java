package com.template;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pigeon";
    private static final String USER = "admin";
    private static final String PASSWORD = "1234";
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void initializeDatabase() {
        createUsersTable();
        createEmailsTable();
        createRecipientsTable();
    }

    public static void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "full_name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_login TIMESTAMP NULL)";
        runStatement(sql);
    }

    public static void createEmailsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS emails (" +
                "email_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "sender_id INT NOT NULL, " +
                "subject VARCHAR(255) NOT NULL, " +
                "body TEXT NOT NULL, " +
                "sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (sender_id) REFERENCES users(user_id))";
        runStatement(sql);
    }

    public static void createRecipientsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS email_recipients (" +
                "recipient_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "email_id INT NOT NULL, " +
                "receiver_id INT NOT NULL, " +
                "is_read BOOLEAN DEFAULT FALSE, " +
                "FOREIGN KEY (email_id) REFERENCES emails(email_id), " +
                "FOREIGN KEY (receiver_id) REFERENCES users(user_id))";
        runStatement(sql);
    }

    private static void runStatement(String sql) {
        try {
            Statement statement = getConnection().createStatement();
            statement.execute(sql);
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int insertUser(String fullName, String email, String passwordHash) {
        try {
            String sql = "INSERT INTO users (full_name, email, password_hash) VALUES (?, ?, ?)";
            PreparedStatement statement = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, fullName);
            statement.setString(2, email.toLowerCase());
            statement.setString(3, passwordHash);
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getUserIdByEmail(String email) {
        try {
            String sql = "SELECT user_id FROM users WHERE email = ?";
            PreparedStatement statement = getConnection().prepareStatement(sql);
            statement.setString(1, email.toLowerCase());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean emailExists(String email) {
        return getUserIdByEmail(email) != -1;
    }

    public static void updateLastLogin(int userId) {
        try {
            String sql = "UPDATE users SET last_login = ? WHERE user_id = ?";
            PreparedStatement statement = getConnection().prepareStatement(sql);
            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean sendEmail(String senderEmail, String receiverEmail, String subject, String body) {
        try {
            int senderId = getUserIdByEmail(senderEmail);
            int receiverId = getUserIdByEmail(receiverEmail);

            if (senderId == -1 || receiverId == -1) {
                return false;
            }

            String sql = "INSERT INTO emails (sender_id, subject, body) VALUES (?, ?, ?)";
            PreparedStatement statement = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, senderId);
            statement.setString(2, subject);
            statement.setString(3, body);
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                int emailId = keys.getInt(1);
                String recipientSql = "INSERT INTO email_recipients (email_id, receiver_id) VALUES (?, ?)";
                PreparedStatement recipientStatement = getConnection().prepareStatement(recipientSql);
                recipientStatement.setInt(1, emailId);
                recipientStatement.setInt(2, receiverId);
                recipientStatement.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<EmailRecord> fetchInbox(String receiverEmail) {
        String sql = getSelectSql() + "WHERE receiver.email = ? ORDER BY e.sent_at DESC";
        return fetchEmails(sql, receiverEmail, "");
    }

    public static ArrayList<EmailRecord> fetchSent(String senderEmail) {
        String sql = getSelectSql() + "WHERE sender.email = ? ORDER BY e.sent_at DESC";
        return fetchEmails(sql, senderEmail, "");
    }

    public static ArrayList<EmailRecord> searchInbox(String receiverEmail, String keyword) {
        String sql = getSelectSql() +
                "WHERE receiver.email = ? AND (LOWER(e.subject) LIKE ? OR LOWER(e.body) LIKE ?) " +
                "ORDER BY e.sent_at DESC";
        return fetchEmails(sql, receiverEmail, keyword);
    }

    public static ArrayList<EmailRecord> searchSent(String senderEmail, String keyword) {
        String sql = getSelectSql() +
                "WHERE sender.email = ? AND (LOWER(e.subject) LIKE ? OR LOWER(e.body) LIKE ?) " +
                "ORDER BY e.sent_at DESC";
        return fetchEmails(sql, senderEmail, keyword);
    }

    private static String getSelectSql() {
        return "SELECT e.email_id, sender.email AS sender_email, receiver.email AS receiver_email, " +
                "e.subject, e.body, e.sent_at, r.is_read " +
                "FROM emails e " +
                "JOIN users sender ON e.sender_id = sender.user_id " +
                "JOIN email_recipients r ON e.email_id = r.email_id " +
                "JOIN users receiver ON r.receiver_id = receiver.user_id ";
    }

    private static ArrayList<EmailRecord> fetchEmails(String sql, String email, String keyword) {
        ArrayList<EmailRecord> emails = new ArrayList<>();
        try {
            PreparedStatement statement = getConnection().prepareStatement(sql);
            statement.setString(1, email.toLowerCase());

            if (!keyword.isEmpty()) {
                statement.setString(2, "%" + keyword.toLowerCase() + "%");
                statement.setString(3, "%" + keyword.toLowerCase() + "%");
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                emails.add(new EmailRecord(
                        resultSet.getInt("email_id"),
                        resultSet.getString("sender_email"),
                        resultSet.getString("receiver_email"),
                        resultSet.getString("subject"),
                        resultSet.getString("body"),
                        String.valueOf(resultSet.getTimestamp("sent_at")),
                        resultSet.getBoolean("is_read")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emails;
    }

    public static void markAsRead(int emailId, String receiverEmail) {
        try {
            String sql = "UPDATE email_recipients r " +
                    "JOIN users u ON r.receiver_id = u.user_id " +
                    "SET r.is_read = TRUE WHERE r.email_id = ? AND u.email = ?";
            PreparedStatement statement = getConnection().prepareStatement(sql);
            statement.setInt(1, emailId);
            statement.setString(2, receiverEmail.toLowerCase());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

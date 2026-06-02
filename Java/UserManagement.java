package com.template;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserManagement {
    String registrationError = "";

    public UserSession registerUser(String fullName, String email, String password) {
        registrationError = "";

        if (!isValidName(fullName)) {
            registrationError = "Name must use letters and spaces only.";
            return null;
        }

        if (!isValidEmail(email)) {
            registrationError = "Enter a valid email address.";
            return null;
        }

        if (password == null || password.isEmpty()) {
            registrationError = "Enter a password.";
            return null;
        }

        if (DatabaseConnection.emailExists(email)) {
            registrationError = "This email is already registered.";
            return null;
        }

        int userId = DatabaseConnection.insertUser(fullName.trim(), email.trim(), hashPassword(password));
        if (userId == -1) {
            registrationError = "Could not save account.";
            return null;
        }

        return new UserSession(userId, fullName.trim(), email.trim().toLowerCase());
    }

    public String getRegistrationError() {
        return registrationError;
    }

    public UserSession login(String email, String password) {
        try {
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            statement.setString(1, email.trim().toLowerCase());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String savedPassword = resultSet.getString("password_hash");

                if (savedPassword.equals(hashPassword(password))) {
                    int userId = resultSet.getInt("user_id");
                    DatabaseConnection.updateLastLogin(userId);
                    return new UserSession(
                            userId,
                            resultSet.getString("full_name"),
                            resultSet.getString("email"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }

    private boolean isValidName(String name) {
        return name != null && name.trim().matches("[A-Za-z ]{2,50}");
    }

    private boolean isValidEmail(String email) {
        return email != null && email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

}

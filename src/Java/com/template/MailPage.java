package com.template;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class MailPage {
    UserSession session;
    boolean inboxPage;
    ObservableList<EmailRecord> emails = FXCollections.observableArrayList();
    ListView<EmailRecord> emailList = new ListView<>(emails);
    TextField searchField = new TextField();
    Label statusLabel = new Label();
    Label fromToLabel = new Label();
    Label subjectLabel = new Label();
    Label dateLabel = new Label();
    TextArea bodyArea = new TextArea();

    public MailPage(UserSession session, boolean inboxPage) {
        this.session = session;
        this.inboxPage = inboxPage;
    }

    public VBox show() {
        searchField.setPromptText("Search subject or body");
        AppStyle.input(searchField);

        Button searchButton = AppStyle.lightButton("Search");
        Button refreshButton = AppStyle.darkButton("Refresh");
        searchButton.setOnAction(e -> loadEmails(searchField.getText()));
        refreshButton.setOnAction(e -> {
            searchField.clear();
            loadEmails("");
        });

        HBox tools = new HBox(10, searchField, searchButton, refreshButton);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        emailList.setPrefWidth(330);
        emailList.getSelectionModel().selectedItemProperty().addListener((obs, oldEmail, newEmail) -> showEmail(newEmail));

        bodyArea.setEditable(false);
        bodyArea.setWrapText(true);
        AppStyle.input(bodyArea);

        VBox details = new VBox(10,
                AppStyle.fieldLabel(inboxPage ? "From" : "To"),
                fromToLabel,
                AppStyle.fieldLabel("Subject"),
                subjectLabel,
                AppStyle.fieldLabel("Date"),
                dateLabel,
                AppStyle.fieldLabel("Body"),
                bodyArea);
        details.setPadding(new Insets(0, 0, 0, 16));
        HBox.setHgrow(details, Priority.ALWAYS);

        HBox content = new HBox(12, emailList, new Separator(Orientation.VERTICAL), details);
        VBox.setVgrow(content, Priority.ALWAYS);

        VBox page = new VBox(14);
        page.setPadding(new Insets(28));
        page.setStyle("-fx-background-color: rgba(255,255,255,0.82);" +
                "-fx-background-radius: 16;" +
                "-fx-border-radius: 16;" +
                "-fx-border-color: rgba(203,213,225,0.75);");
        page.getChildren().addAll(AppStyle.title(inboxPage ? "Inbox" : "Sent Emails"), tools, content, statusLabel);

        loadEmails("");
        return page;
    }

    private void loadEmails(String keyword) {
        statusLabel.setStyle("-fx-text-fill: #64748B;");
        statusLabel.setText("Loading...");

        Task<ArrayList<EmailRecord>> task = new Task<>() {
            protected ArrayList<EmailRecord> call() {
                if (inboxPage) {
                    if (keyword == null || keyword.trim().isEmpty()) {
                        return DatabaseConnection.fetchInbox(session.email);
                    }
                    return DatabaseConnection.searchInbox(session.email, keyword.trim());
                }

                if (keyword == null || keyword.trim().isEmpty()) {
                    return DatabaseConnection.fetchSent(session.email);
                }
                return DatabaseConnection.searchSent(session.email, keyword.trim());
            }
        };

        task.setOnSucceeded(e -> {
            emails.setAll(task.getValue());
            clearDetails();
            statusLabel.setStyle("-fx-text-fill: " + AppStyle.SUCCESS + ";");
            statusLabel.setText(emails.size() + " email(s) found.");
        });

        task.setOnFailed(e -> {
            statusLabel.setStyle("-fx-text-fill: " + AppStyle.ERROR + ";");
            statusLabel.setText("Could not load emails.");
        });

        new Thread(task).start();
    }

    private void showEmail(EmailRecord email) {
        if (email == null) {
            clearDetails();
            return;
        }

        if (inboxPage) {
            fromToLabel.setText(email.senderEmail);
            DatabaseConnection.markAsRead(email.id, session.email);
        } else {
            fromToLabel.setText(email.receiverEmail);
        }

        subjectLabel.setText(email.subject);
        dateLabel.setText(email.sentAt);
        bodyArea.setText(email.body);
    }

    private void clearDetails() {
        fromToLabel.setText("");
        subjectLabel.setText("");
        dateLabel.setText("");
        bodyArea.clear();
    }
}

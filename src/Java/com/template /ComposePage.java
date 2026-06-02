package com.template;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ComposePage {
    UserSession session;
    TextField toField = new TextField();
    TextField subjectField = new TextField();
    TextArea bodyArea = new TextArea();
    Label messageLabel = new Label();
    Button sendButton = AppStyle.darkButton("Send Email");

    public ComposePage(UserSession session) {
        this.session = session;
    }

    public VBox show() {
        TextField fromField = new TextField(session.email);
        fromField.setEditable(false);

        toField.setPromptText("receiver@pigeon.com");
        subjectField.setPromptText("Subject");
        bodyArea.setPromptText("Write your message");
        bodyArea.setWrapText(true);
        bodyArea.setPrefRowCount(12);

        AppStyle.input(fromField);
        AppStyle.input(toField);
        AppStyle.input(subjectField);
        AppStyle.input(bodyArea);

        sendButton.setOnAction(e -> sendEmail());

        Button clearButton = AppStyle.lightButton("Clear");
        clearButton.setOnAction(e -> clear());

        messageLabel.setStyle("-fx-text-fill: " + AppStyle.ERROR + ";");

        VBox page = pageBox();
        page.getChildren().addAll(
                AppStyle.title("Compose"),
                fieldBox("From", fromField),
                fieldBox("To", toField),
                fieldBox("Subject", subjectField),
                fieldBox("Body", bodyArea),
                sendButton,
                clearButton,
                messageLabel);
        return page;
    }

    private VBox pageBox() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(28));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.82);" +
                "-fx-background-radius: 16;" +
                "-fx-border-radius: 16;" +
                "-fx-border-color: rgba(203,213,225,0.75);");
        return box;
    }

    private VBox fieldBox(String label, Control field) {
        return new VBox(6, AppStyle.fieldLabel(label), field);
    }

    private void sendEmail() {
        String receiver = toField.getText().trim();
        String subject = subjectField.getText().trim();
        String body = bodyArea.getText().trim();

        if (receiver.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: " + AppStyle.ERROR + ";");
            messageLabel.setText("Receiver email, subject and body are required.");
            return;
        }

        sendButton.setDisable(true);
        sendButton.setText("Sending...");

        Task<Boolean> task = new Task<>() {
            protected Boolean call() {
                return DatabaseConnection.sendEmail(session.email, receiver, subject, body);
            }
        };

        task.setOnSucceeded(e -> {
            sendButton.setDisable(false);
            sendButton.setText("Send Email");

            if (task.getValue()) {
                clear();
                messageLabel.setStyle("-fx-text-fill: " + AppStyle.SUCCESS + ";");
                messageLabel.setText("Email sent successfully.");
            } else {
                messageLabel.setStyle("-fx-text-fill: " + AppStyle.ERROR + ";");
                messageLabel.setText("Receiver email account was not found.");
            }
        });

        task.setOnFailed(e -> {
            sendButton.setDisable(false);
            sendButton.setText("Send Email");
            messageLabel.setStyle("-fx-text-fill: " + AppStyle.ERROR + ";");
            messageLabel.setText("Could not send email.");
        });

        new Thread(task).start();
    }

    private void clear() {
        toField.clear();
        subjectField.clear();
        bodyArea.clear();
        messageLabel.setText("");
    }
}

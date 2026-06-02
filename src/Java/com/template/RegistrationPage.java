package com.template;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegistrationPage {
    TextField nameField = new TextField();
    TextField emailField = new TextField();
    PasswordField passwordField = new PasswordField();
    Label messageLabel = new Label();
    Button registerButton = AppStyle.darkButton("Create account");
    UserManagement userManagement = new UserManagement();

    public void show(Stage stage) {
        nameField.setPromptText("Full name");
        emailField.setPromptText("example@pigeon.com");
        passwordField.setPromptText("Password");
        AppStyle.input(nameField);
        AppStyle.input(emailField);
        AppStyle.input(passwordField);
        filterName();
        filterEmail();

        Hyperlink loginLink = new Hyperlink("Sign in");
        loginLink.setOnAction(e -> new LoginPage().show(stage));

        HBox footer = new HBox(6, AppStyle.smallText("Already have an account?"), loginLink);
        footer.setAlignment(Pos.CENTER);

        registerButton.setOnAction(e -> register(stage));
        messageLabel.setStyle("-fx-text-fill: " + AppStyle.ERROR + ";");

        VBox card = AppStyle.card();
        card.getChildren().addAll(
                AppStyle.logo(145),
                AppStyle.title("Create account"),
                AppStyle.smallText("Join Pigeon Mail"),
                fieldBox("Full name", nameField),
                fieldBox("Email address", emailField),
                fieldBox("Password", passwordField),
                registerButton,
                messageLabel,
                footer);

        stage.setTitle("Pigeon Mail - Register");
        stage.setScene(AppStyle.makeScene(card, 500, 740));
        stage.show();
    }

    private VBox fieldBox(String label, TextField field) {
        return new VBox(6, AppStyle.fieldLabel(label), field);
    }

    private void register(Stage stage) {
        String error = checkFields();
        if (!error.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: " + AppStyle.ERROR + ";");
            messageLabel.setText(error);
            return;
        }

        registerButton.setDisable(true);
        registerButton.setText("Creating...");

        Task<UserSession> task = new Task<>() {
            protected UserSession call() {
                return userManagement.registerUser(
                        nameField.getText().trim(),
                        emailField.getText().trim(),
                        passwordField.getText());
            }
        };

        task.setOnSucceeded(e -> {
            registerButton.setDisable(false);
            registerButton.setText("Create account");

            UserSession session = task.getValue();
            if (session != null) {
                new MainPage(session).show(stage);
            } else {
                messageLabel.setText(userManagement.getRegistrationError());
            }
        });

        task.setOnFailed(e -> {
            registerButton.setDisable(false);
            registerButton.setText("Create account");
            messageLabel.setText("Connection error.");
        });

        new Thread(task).start();
    }

    private void filterName() {
        nameField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("[A-Za-z ]*")) {
                nameField.setText(newText.replaceAll("[^A-Za-z ]", ""));
            }
        });
    }

    private void filterEmail() {
        emailField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.contains(" ")) {
                emailField.setText(newText.replace(" ", ""));
            }
        });
    }

    private String checkFields() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return "Please fill all fields.";
        }

        if (!name.matches("[A-Za-z ]{2,50}")) {
            return "Name must use letters and spaces only.";
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "Enter a valid email address.";
        }

        return "";
    }
}

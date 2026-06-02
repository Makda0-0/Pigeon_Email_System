package com.template;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage {
    TextField emailField = new TextField();
    PasswordField passwordField = new PasswordField();
    Label messageLabel = new Label();
    Button loginButton = AppStyle.darkButton("Login");
    UserManagement userManagement = new UserManagement();

    public void show(Stage stage) {
        emailField.setPromptText("example@pigeon.com");
        passwordField.setPromptText("Password");
        AppStyle.input(emailField);
        AppStyle.input(passwordField);

        Hyperlink registerLink = new Hyperlink("Create account");
        registerLink.setOnAction(e -> new RegistrationPage().show(stage));

        HBox footer = new HBox(6, AppStyle.smallText("Do not have an account?"), registerLink);
        footer.setAlignment(Pos.CENTER);

        loginButton.setOnAction(e -> login(stage));
        messageLabel.setStyle("-fx-text-fill: " + AppStyle.ERROR + ";");

        VBox card = AppStyle.card();
        card.getChildren().addAll(
                AppStyle.logo(145),
                AppStyle.title("Pigeon Mail"),
                AppStyle.smallText("Sign in to continue"),
                fieldBox("Email address", emailField),
                fieldBox("Password", passwordField),
                loginButton,
                messageLabel,
                footer);

        stage.setTitle("Pigeon Mail - Login");
        stage.setScene(AppStyle.makeScene(card, 500, 720));
        stage.show();
    }

    private VBox fieldBox(String label, TextField field) {
        return new VBox(6, AppStyle.fieldLabel(label), field);
    }

    private void login(Stage stage) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter email and password.");
            return;
        }

        loginButton.setDisable(true);
        loginButton.setText("Logging in...");

        Task<UserSession> task = new Task<>() {
            protected UserSession call() {
                return userManagement.login(email, password);
            }
        };

        task.setOnSucceeded(e -> {
            loginButton.setDisable(false);
            loginButton.setText("Login");

            UserSession session = task.getValue();
            if (session != null) {
                new MainPage(session).show(stage);
            } else {
                messageLabel.setText("Invalid email or password.");
            }
        });

        task.setOnFailed(e -> {
            loginButton.setDisable(false);
            loginButton.setText("Login");
            messageLabel.setText("Connection error.");
        });

        new Thread(task).start();
    }
}

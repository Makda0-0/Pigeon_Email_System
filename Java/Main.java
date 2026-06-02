package com.template;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        DatabaseConnection.initializeDatabase();
        LoginPage loginPage = new LoginPage();
        loginPage.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

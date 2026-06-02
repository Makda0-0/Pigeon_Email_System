package com.template;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainPage {
    UserSession session;
    BorderPane root = new BorderPane();

    public MainPage(UserSession session) {
        this.session = session;
    }

    public void show(Stage stage) {
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #E0F2FE, #F8FAFC, #F1F5F9);");

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = AppStyle.title("Pigeon Mail");
        Label account = AppStyle.smallText(session.fullName + " - " + session.email);
        Button logoutButton = AppStyle.lightButton("Logout");
        logoutButton.setOnAction(e -> new LoginPage().show(stage));

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(AppStyle.logo(72), title, account, spacer, logoutButton);

        VBox menu = new VBox(12);
        menu.setPadding(new Insets(24, 18, 24, 0));
        menu.setPrefWidth(170);

        Button mainButton = menuButton("Main");
        Button composeButton = menuButton("Compose");
        Button inboxButton = menuButton("Inbox");
        Button sentButton = menuButton("Sent");

        mainButton.setOnAction(e -> showHome());
        composeButton.setOnAction(e -> root.setCenter(new ComposePage(session).show()));
        inboxButton.setOnAction(e -> root.setCenter(new MailPage(session, true).show()));
        sentButton.setOnAction(e -> root.setCenter(new MailPage(session, false).show()));

        menu.getChildren().addAll(mainButton, composeButton, inboxButton, sentButton);

        root.setTop(header);
        root.setLeft(menu);
        showHome();

        stage.setTitle("Pigeon Mail");
        stage.setScene(new Scene(root, 980, 680));
        stage.show();
    }

    private Button menuButton(String text) {
        Button button = AppStyle.lightButton(text);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private void showHome() {
        VBox home = new VBox(18);
        home.setAlignment(Pos.CENTER);
        home.setPadding(new Insets(40));
        home.setStyle("-fx-background-color: rgba(255,255,255,0.75);" +
                "-fx-background-radius: 16;" +
                "-fx-border-radius: 16;" +
                "-fx-border-color: rgba(203,213,225,0.75);");

        Button compose = AppStyle.darkButton("Compose Email");
        Button inbox = AppStyle.lightButton("Open Inbox");
        Button sent = AppStyle.lightButton("Open Sent Emails");
        compose.setMaxWidth(260);
        inbox.setMaxWidth(260);
        sent.setMaxWidth(260);

        compose.setOnAction(e -> root.setCenter(new ComposePage(session).show()));
        inbox.setOnAction(e -> root.setCenter(new MailPage(session, true).show()));
        sent.setOnAction(e -> root.setCenter(new MailPage(session, false).show()));

        home.getChildren().addAll(
                AppStyle.logo(150),
                AppStyle.title("Main Page"),
                AppStyle.smallText("Choose what you want to do."),
                compose,
                inbox,
                sent);

        root.setCenter(home);
    }
}

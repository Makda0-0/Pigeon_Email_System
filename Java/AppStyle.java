package com.template;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

public class AppStyle {
    public static final String ERROR = "#EF4444";
    public static final String SUCCESS = "#10B981";

    public static Scene makeScene(VBox card, double width, double height) {
        StackPane root = new StackPane();

        Rectangle background = new Rectangle();
        background.widthProperty().bind(root.widthProperty());
        background.heightProperty().bind(root.heightProperty());
        background.setFill(new LinearGradient(0, 0, 1, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#E0F2FE")),
                new Stop(0.5, Color.web("#F8FAFC")),
                new Stop(1, Color.web("#F1F5F9"))));

        root.getChildren().addAll(background, card);
        return new Scene(root, width, height);
    }

    public static VBox card() {
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(430);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.88);" +
                "-fx-background-radius: 18;" +
                "-fx-padding: 36;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(255,255,255,0.7);");
        return card;
    }

    public static ImageView logo(double width) {
        Image image = new Image(AppStyle.class.getResource("/images/pigeon-mail-logo-transparent.png").toExternalForm());
        ImageView logo = new ImageView(image);
        logo.setFitWidth(width);
        logo.setPreserveRatio(true);
        return logo;
    }

    public static Label title(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        return label;
    }

    public static Label smallText(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");
        return label;
    }

    public static Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        return label;
    }

    public static void input(TextInputControl input) {
        input.setStyle("-fx-background-color: #F8FAFC;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 14 10 14;" +
                "-fx-font-size: 14px;");
    }

    public static Button darkButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-color: #0F172A;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 11 16 11 16;");
        return button;
    }

    public static Button lightButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #E0F2FE;" +
                "-fx-text-fill: #0F172A;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 16 10 16;");
        return button;
    }
}

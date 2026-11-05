package com.shuffly.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.function.Consumer;

public class Sidebar extends VBox {

    private final Button homeBtn, libraryBtn, shuffleModeBtn, queueBtn, settingsBtn;
    private final Consumer<String> onNavigate;

    public Sidebar(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;

        setPrefWidth(240);
        setSpacing(8);
        setStyle("-fx-background-color: #000000;");
        setPadding(new Insets(24, 12, 24, 12));

        Label logo = new Label("Shuffly");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("System", FontWeight.BOLD, 26));
        logo.setPadding(new Insets(0, 0, 28, 12));

        homeBtn = navButton("Home");
        libraryBtn = navButton("Library");
        shuffleModeBtn = navButton("Shuffle Mode");
        queueBtn = navButton("Queue");
        settingsBtn = navButton("Settings");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(logo, homeBtn, libraryBtn, shuffleModeBtn, queueBtn, spacer, settingsBtn);
        select("Home");
    }

    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        applyBaseStyle(btn);

        btn.setOnAction(e -> onNavigate.accept(text));

        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#282828")) applyHoverStyle(btn);
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("#282828")) applyBaseStyle(btn);
        });

        return btn;
    }

    public void select(String pageName) {
        Button[] all = {homeBtn, libraryBtn, shuffleModeBtn, queueBtn, settingsBtn};
        for (Button b : all) applyBaseStyle(b);

        switch (pageName) {
            case "Home" -> applySelectedStyle(homeBtn);
            case "Library" -> applySelectedStyle(libraryBtn);
            case "Shuffle Mode" -> applySelectedStyle(shuffleModeBtn);
            case "Queue" -> applySelectedStyle(queueBtn);
            case "Settings" -> applySelectedStyle(settingsBtn);
        }
    }

    private void applyBaseStyle(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #b3b3b3; " +
            "-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12 16; " +
            "-fx-background-radius: 6px; -fx-cursor: hand; " +
            "-fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        );
    }

    private void applyHoverStyle(Button btn) {
        btn.setStyle(
            "-fx-background-color: #181818; -fx-text-fill: white; " +
            "-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12 16; " +
            "-fx-background-radius: 6px; -fx-cursor: hand; " +
            "-fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        );
    }

    private void applySelectedStyle(Button btn) {
        btn.setStyle(
            "-fx-background-color: #282828; -fx-text-fill: white; " +
            "-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12 16; " +
            "-fx-background-radius: 6px; -fx-cursor: hand; " +
            "-fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        );
    }
}
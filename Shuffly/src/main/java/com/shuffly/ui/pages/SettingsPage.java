package com.shuffly.ui.pages;

import com.shuffly.AppState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SettingsPage extends VBox {

    public SettingsPage(AppState state, Runnable onColorChanged) {
        setSpacing(20);
        setPadding(new Insets(10, 0, 0, 0));

        Label sectionTitle = new Label("Accent Color");
        sectionTitle.setTextFill(Color.WHITE);
        sectionTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));

        HBox colorOptions = new HBox(15);
        colorOptions.setAlignment(Pos.CENTER_LEFT);

        String[] colors = {
            "#1db954", // green
            "#3b82f6", // blue
            "#a855f7", // purple
            "#ec4899", // pink
            "#f59e0b", // amber
            "#ef4444"  // red
        };

        for (String hex : colors) {
            Button colorBtn = new Button();
            colorBtn.setPrefSize(36, 36);

            boolean isSelected = state.accentColor.toString().toLowerCase()
                    .contains(hex.substring(1).toLowerCase());

            colorBtn.setStyle(
                "-fx-background-color: " + hex + ";" +
                "-fx-background-radius: 50%;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: " + (isSelected ? "white" : "transparent") + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 50%;"
            );

            colorBtn.setOnAction(e -> {
                state.accentColor = Color.web(hex);
                if (onColorChanged != null) {
                    onColorChanged.run();
                }
            });

            colorOptions.getChildren().add(colorBtn);
        }

        getChildren().addAll(sectionTitle, colorOptions);
    }
}
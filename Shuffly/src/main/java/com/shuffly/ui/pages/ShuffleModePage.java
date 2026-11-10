package com.shuffly.ui.pages;

import com.shuffly.AppState;
import com.shuffly.ShuffleMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ShuffleModePage extends VBox {

    public ShuffleModePage(AppState state, Runnable onModeChanged) {
        setSpacing(20);
        setPadding(new Insets(10, 0, 20, 0));

        Label info = new Label("Choose how Shuffly orders your music");
        info.setTextFill(Color.web("#b3b3b3"));
        info.setFont(Font.font(14));

        VBox options = new VBox(10);

        for (ShuffleMode shuffleMode : ShuffleMode.values()) {
            Button modeBtn = new Button(shuffleMode.getLabel());
            modeBtn.setMaxWidth(Double.MAX_VALUE);
            modeBtn.setAlignment(Pos.CENTER_LEFT);

            String baseStyle =
                "-fx-background-color: #181818; -fx-text-fill: white; " +
                "-fx-font-size: 15px; -fx-padding: 14 18; " +
                "-fx-background-radius: 8px; -fx-cursor: hand;";

            String hoverStyle =
                "-fx-background-color: #282828; -fx-text-fill: white; " +
                "-fx-font-size: 15px; -fx-padding: 14 18; " +
                "-fx-background-radius: 8px; -fx-cursor: hand;";

            modeBtn.setStyle(baseStyle);

            modeBtn.setOnAction(e -> {
                state.currentShuffleMode = shuffleMode.getLabel();
                state.queueManager.setShuffleMode(shuffleMode);
                if (onModeChanged != null) {
                    onModeChanged.run();
                }
            });

            modeBtn.setOnMouseEntered(e -> modeBtn.setStyle(hoverStyle));
            modeBtn.setOnMouseExited(e -> modeBtn.setStyle(baseStyle));

            options.getChildren().add(modeBtn);
        }

        getChildren().addAll(info, options);
    }
}
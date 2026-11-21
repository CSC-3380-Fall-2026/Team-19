package com.shuffly.ui.pages;

import com.shuffly.AppState;
import com.shuffly.shuffle.ShuffleMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

public class ShuffleModePage extends VBox {

    private final String[] MOODS = {
        "Happy", "Chill", "Sad", "Energetic",
        "Focus", "Party", "Romantic", "Angry"
    };

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

            // Highlight current mode
            boolean selected = shuffleMode.getLabel().equals(state.currentShuffleMode);
            modeBtn.setStyle(selected
                ? "-fx-background-color: #1db954; -fx-text-fill: black; -fx-font-size: 15px; -fx-padding: 14 18; -fx-background-radius: 8px; -fx-cursor: hand;"
                : baseStyle);

            modeBtn.setOnAction(e -> {
                if (shuffleMode == ShuffleMode.MOOD_BASED) {
                    showMoodPopup(state, onModeChanged);
                } else {
                    state.currentShuffleMode = shuffleMode.getLabel();
                    state.queueManager.setShuffleMode(shuffleMode);
                    if (onModeChanged != null) onModeChanged.run();

                    // rebuild to update selected styles
                    getChildren().setAll(
                        new ShuffleModePage(state, onModeChanged).getChildren()
                    );
                }
            });

            if (!selected) {
                modeBtn.setOnMouseEntered(e -> modeBtn.setStyle(hoverStyle));
                modeBtn.setOnMouseExited(e -> modeBtn.setStyle(baseStyle));
            }

            options.getChildren().add(modeBtn);
        }

        getChildren().addAll(info, options);
    }

    private void showMoodPopup(AppState state, Runnable onModeChanged) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Select Your Mood");

        VBox root = new VBox(18);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #181818;");

        Label title = new Label("What's your mood?");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font(18));

        FlowPane chips = new FlowPane(10, 10);
        chips.setAlignment(Pos.CENTER_LEFT);

        for (String mood : MOODS) {
            Button chip = new Button(mood);
            chip.setStyle(
                "-fx-background-color: #282828; -fx-text-fill: white; " +
                "-fx-background-radius: 20px; -fx-padding: 8 16; -fx-cursor: hand;"
            );

            chip.setOnAction(e -> {
                applyMood(state, mood, onModeChanged);
                dialog.close();
            });

            chips.getChildren().add(chip);
        }

        // Optional custom mood
        Button customBtn = new Button("Type a mood...");
        customBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #1db954; " +
            "-fx-cursor: hand; -fx-font-size: 13px;"
        );
        customBtn.setOnAction(e -> {
            TextInputDialog input = new TextInputDialog(state.selectedMood);
            input.setTitle("Custom Mood");
            input.setHeaderText("Describe your mood");
            input.setContentText("Mood:");
            input.showAndWait().ifPresent(text -> {
                if (text != null && !text.isBlank()) {
                    applyMood(state, text.trim(), onModeChanged);
                    dialog.close();
                }
            });
        });

        Button cancel = new Button("Cancel");
        cancel.setStyle(
            "-fx-background-color: #282828; -fx-text-fill: white; " +
            "-fx-background-radius: 8px; -fx-padding: 8 16; -fx-cursor: hand;"
        );
        cancel.setOnAction(e -> dialog.close());

        HBox bottom = new HBox(12, customBtn, cancel);
        bottom.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(title, chips, bottom);

        dialog.setScene(new Scene(root, 420, 260));
        dialog.showAndWait();
    }


    private void applyMood(AppState state, String mood, Runnable onModeChanged) {
        state.selectedMood = mood;
        state.currentShuffleMode = "Mood-based (" + mood + ")";

        state.queueManager.setShuffleMode(ShuffleMode.MOOD_BASED);

        // Rebuild the queue using the selected mood
        if (state.selectedPlaylist != null) {
            state.queueManager.loadShuffled(
                state.selectedPlaylist.getTracks(),
                mood
            );
        }

        if (onModeChanged != null) {
            onModeChanged.run();
        }
    }
}
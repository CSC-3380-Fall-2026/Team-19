package com.shuffly.ui;

import com.shuffly.AppState;
import com.shuffly.model.Track;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PlayerBar extends HBox {

    private final AppState state;
    private final Label songTitleLabel;
    private final Label artistNameLabel;
    private final Label shuffleLabel;
    private final Button playBtn;

    public PlayerBar(AppState state) {
        this.state = state;

        setPrefHeight(90);
        setSpacing(20);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(0, 20, 0, 20));
        setStyle("-fx-background-color: #181818; -fx-border-color: #282828; -fx-border-width: 1 0 0 0;");

        // Left: album art + song info
        HBox left = new HBox(15);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPrefWidth(320);

        Label albumArt = new Label("♪");
        albumArt.setPrefSize(56, 56);
        albumArt.setAlignment(Pos.CENTER);
        albumArt.setStyle("-fx-background-color: #333333; -fx-text-fill: #b3b3b3; -fx-font-size: 24px; -fx-background-radius: 4px;");

        songTitleLabel = new Label("No song playing");
        songTitleLabel.setTextFill(Color.WHITE);
        songTitleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

        artistNameLabel = new Label("—");
        artistNameLabel.setTextFill(Color.web("#b3b3b3"));
        artistNameLabel.setFont(Font.font(12));

        VBox songInfo = new VBox(3, songTitleLabel, artistNameLabel);
        left.getChildren().addAll(albumArt, songInfo);

        // Center: controls + progress
        VBox center = new VBox(8);
        center.setAlignment(Pos.CENTER);
        HBox.setHgrow(center, Priority.ALWAYS);

        Button prevBtn = controlButton("⏮");
        playBtn = playButton();
        Button nextBtn = controlButton("⏭");

        prevBtn.setOnAction(e -> {
            state.queueManager.previous();
            refresh();
        });
        nextBtn.setOnAction(e -> {
            state.queueManager.next();
            refresh();
        });
        playBtn.setOnAction(e -> {
            Track current = state.queueManager.getCurrent();
            if (current == null) {
                if (!state.queueManager.getUpNext().isEmpty()) {
                    state.queueManager.next();
                    state.isPlaying = true;
                }
            } else {
                state.isPlaying = !state.isPlaying;
            }
            refresh();
        });

        HBox controls = new HBox(20, prevBtn, playBtn, nextBtn);
        controls.setAlignment(Pos.CENTER);

        Label currentTime = new Label("0:00");
        currentTime.setTextFill(Color.web("#b3b3b3"));
        currentTime.setFont(Font.font(11));
        currentTime.setMinWidth(35);

        Slider slider = new Slider(0, 100, 30);
        slider.setPrefWidth(400);
        slider.setStyle("-fx-control-inner-background: #4d4d4d;");

        Label totalTime = new Label("3:45");
        totalTime.setTextFill(Color.web("#b3b3b3"));
        totalTime.setFont(Font.font(11));
        totalTime.setMinWidth(35);

        HBox progress = new HBox(10, currentTime, slider, totalTime);
        progress.setAlignment(Pos.CENTER);

        center.getChildren().addAll(controls, progress);

        // Right: shuffle pill
        shuffleLabel = new Label("Shuffle: " + state.currentShuffleMode);
        shuffleLabel.setTextFill(state.accentColor);
        shuffleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        shuffleLabel.setStyle("-fx-padding: 6 12; -fx-background-color: #282828; -fx-background-radius: 20;");

        HBox right = new HBox(shuffleLabel);
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setPrefWidth(220);

        getChildren().addAll(left, center, right);
    }

    public void refresh() {
        Track current = state.queueManager.getCurrent();
        if (current != null) {
            songTitleLabel.setText(current.getName());
            artistNameLabel.setText(current.getArtist());
        } else {
            songTitleLabel.setText("No song playing");
            artistNameLabel.setText("—");
            state.isPlaying = false;
        }

        playBtn.setText(state.isPlaying ? "⏸" : "▶");
        shuffleLabel.setText("Shuffle: " + state.currentShuffleMode);
        shuffleLabel.setTextFill(state.accentColor);
    }

    private Button playButton() {
        Button btn = new Button("▶");
        String style =
            "-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; " +
            "-fx-cursor: hand; -fx-background-radius: 50%; " +
            "-fx-min-width: 48px; -fx-min-height: 48px; -fx-max-width: 48px; -fx-max-height: 48px; " +
            "-fx-focus-color: transparent; -fx-faint-focus-color: transparent;";
        btn.setStyle(style);
        return btn;
    }

    private Button controlButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #b3b3b3; -fx-font-size: 16px; " +
            "-fx-cursor: hand; -fx-padding: 8; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; " +
            "-fx-cursor: hand; -fx-padding: 8; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #b3b3b3; -fx-font-size: 16px; " +
            "-fx-cursor: hand; -fx-padding: 8; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        ));
        return btn;
    }
}
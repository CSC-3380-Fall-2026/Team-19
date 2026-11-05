package com.shuffly.ui.pages;

import com.shuffly.AppState;
import com.shuffly.model.Playlist;
import com.shuffly.model.Track;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PlaylistDetailPage extends VBox {

    public PlaylistDetailPage(AppState state, Runnable onBack) {
        setSpacing(20);
        setPadding(new Insets(10, 0, 20, 0));

        Playlist playlist = state.selectedPlaylist;

        // Back button
        Button backBtn = new Button("← Back to Library");
        backBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #b3b3b3;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        // Title
        String titleText = playlist != null ? playlist.getName() : "Playlist";
        Label title = new Label(titleText);
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        int count = playlist != null ? playlist.getTrackCount() : 0;
        Label subtitle = new Label(count + " songs");
        subtitle.setTextFill(Color.web("#b3b3b3"));
        subtitle.setFont(Font.font(14));

        // Play button for this playlist
        Button playAllBtn = new Button("Play");
        playAllBtn.setStyle(
            "-fx-background-color: #1db954;" +
            "-fx-text-fill: black;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20px;" +
            "-fx-padding: 10 24;" +
            "-fx-cursor: hand;"
        );
        playAllBtn.setOnAction(e -> {
            if (playlist != null && !playlist.getTracks().isEmpty()) {
                state.queueManager.loadPlaylist(playlist.getTracks(), true);
                state.isPlaying = true;
            }
        });

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(title, playAllBtn);

        // Track list
        VBox trackList = new VBox(8);
        if (playlist == null || playlist.getTracks().isEmpty()) {
            Label empty = new Label("No songs in this playlist");
            empty.setTextFill(Color.web("#b3b3b3"));
            trackList.getChildren().add(empty);
        } else {
            int index = 1;
            for (Track track : playlist.getTracks()) {
                trackList.getChildren().add(createTrackRow(index++, track, state));
            }
        }

        ScrollPane scroll = new ScrollPane(trackList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setPrefHeight(450);

        getChildren().addAll(backBtn, header, subtitle, scroll);
    }

    private HBox createTrackRow(int number, Track track, AppState state) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-radius: 6px;");
        row.setCursor(javafx.scene.Cursor.HAND);

        Label num = new Label(String.valueOf(number));
        num.setTextFill(Color.web("#b3b3b3"));
        num.setMinWidth(28);

        VBox info = new VBox(2);
        Label name = new Label(track.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

        Label artist = new Label(track.getArtist());
        artist.setTextFill(Color.web("#b3b3b3"));
        artist.setFont(Font.font(12));

        info.getChildren().addAll(name, artist);

        row.getChildren().addAll(num, info);

        row.setOnMouseEntered(e ->
            row.setStyle("-fx-background-color: #282828; -fx-background-radius: 6px;")
        );
        row.setOnMouseExited(e ->
            row.setStyle("-fx-background-radius: 6px;")
        );

        // Click a single song → make it current / play next behavior can be improved later
        row.setOnMouseClicked(e -> {
            state.queueManager.loadPlaylist(java.util.List.of(track), false);
            state.isPlaying = true;
        });

        return row;
    }
}
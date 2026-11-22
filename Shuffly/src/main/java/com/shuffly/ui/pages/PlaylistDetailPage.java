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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.shuffly.spotify.SpotifyApiClient;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class PlaylistDetailPage extends VBox {

    public PlaylistDetailPage(AppState state, Runnable onBack) {
        setSpacing(16);
        setPadding(new Insets(10, 0, 20, 0));
        setFillWidth(true);

        Playlist playlist = state.selectedPlaylist;

        // Back
        Button backBtn = new Button("← Back to Library");
        backBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #b3b3b3;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0 0 8 0;"
        );
        backBtn.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        // Header
        String titleText = playlist != null ? playlist.getName() : "Playlist";
        Label title = new Label(titleText);
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setWrapText(true);
        title.setMaxWidth(700);

        int count = playlist != null ? playlist.getTrackCount() : 0;
        Label subtitle = new Label(count + " songs");
        subtitle.setTextFill(Color.web("#b3b3b3"));
        subtitle.setFont(Font.font(14));

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

                // Exit Station Mode
                state.stationMode = false;
                state.selectedStation = null;
                state.stationPlayer.clear();

                // Load the playlist
                state.queueManager.loadShuffled(
                    playlist.getTracks(),
                    state.selectedMood
                );

                // Start the first song on Spotify
                Track firstTrack = state.queueManager.getCurrent();

                if (firstTrack != null) {
                    state.isPlaying = true;
                    startSpotifyTrack(firstTrack);
                }
            }
        });

        HBox headerRow = new HBox(16);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerRow.getChildren().addAll(title, spacer, playAllBtn);

        // Tracks
        VBox trackList = new VBox(6);
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
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setPannable(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        scroll.setPrefHeight(480);

        getChildren().addAll(backBtn, headerRow, subtitle, scroll);
    }

    private HBox createTrackRow(int number, Track track, AppState state) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-radius: 6px;");
        row.setCursor(javafx.scene.Cursor.HAND);
        row.setMaxWidth(Double.MAX_VALUE);

        Label num = new Label(String.valueOf(number));
        num.setTextFill(Color.web("#b3b3b3"));
        num.setMinWidth(28);

        VBox info = new VBox(2);
        Label name = new Label(track.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        name.setWrapText(true);

        Label artist = new Label(track.getArtist());
        artist.setTextFill(Color.web("#b3b3b3"));
        artist.setFont(Font.font(12));

        info.getChildren().addAll(name, artist);
        HBox.setHgrow(info, Priority.ALWAYS);

        row.getChildren().addAll(num, info);

        row.setOnMouseEntered(e ->
            row.setStyle("-fx-background-color: #282828; -fx-background-radius: 6px;")
        );
        row.setOnMouseExited(e ->
            row.setStyle("-fx-background-radius: 6px;")
        );

        row.setOnMouseClicked(e -> {

            // Exit Station Mode
            state.stationMode = false;
            state.selectedStation = null;
            state.stationPlayer.clear();

            // Load the selected song
            state.queueManager.loadPlaylist(
                java.util.List.of(track),
                false
            );

            state.isPlaying = true;

            // Start playback on Spotify
            startSpotifyTrack(track);
        });

        return row;
    }
    
    private void startSpotifyTrack(Track track) {

        if (track == null) {
            return;
        }

        new Thread(() -> {
            try {
                SpotifyApiClient api = new SpotifyApiClient();
                String deviceId = api.getActiveDeviceId();

                api.playTracks(
                    java.util.List.of(track.getId()),
                    deviceId
                );

            } catch (Exception ex) {
                ex.printStackTrace();

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Playback Error");
                    alert.setHeaderText("Couldn't start playback");
                    alert.setContentText(
                        "Make sure Spotify is open and an active device is available."
                    );
                    alert.showAndWait();
                });
            }
        }).start();
    }
}
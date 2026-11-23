
package com.shuffly.ui.pages;

import com.shuffly.AppState;
import com.shuffly.model.Track;
import com.shuffly.spotify.SpotifyApiClient;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.concurrent.Task;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.List;

public class SearchPage extends VBox {

    private final AppState state;
    private final VBox resultsContainer = new VBox(6);
    private final Runnable onPlayed;
    private int searchRequestId = 0;


    public SearchPage(AppState state) {
        this(state, null);
    }

    public SearchPage(AppState state, Runnable onPlayed) {
        this.state = state;
        this.onPlayed = onPlayed;

        // Layout settings
        setSpacing(18);
        setPadding(new Insets(10, 0, 20, 0));
        setStyle("-fx-background-color: transparent;");

        // Page title
        Label title = new Label("Search");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search songs or artists...");
        searchField.setPrefWidth(420);
        searchField.setMaxWidth(520);
        searchField.setStyle(
            "-fx-background-color: #282828;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #b3b3b3;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 12 14;" +
            "-fx-font-size: 14px;"
        );

        // Results heading
        Label resultsTitle = new Label("Search Results");
        resultsTitle.setTextFill(Color.WHITE);
        resultsTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));
        resultsTitle.setPadding(new Insets(8, 0, 0, 0));

        // Results container
        resultsContainer.setPadding(new Insets(4, 0, 0, 0));

        // Scrollable results
        ScrollPane scroll = new ScrollPane(resultsContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background: transparent;"
        );
        scroll.setPrefHeight(450);
        scroll.setPannable(true);

        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Live search with a short delay
        PauseTransition searchDelay = new PauseTransition(
            Duration.millis(350)
        );

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            int requestId = ++searchRequestId;

            searchDelay.stop();
            resultsContainer.getChildren().clear();

            if (newVal == null || newVal.isBlank()) {
                showMessage("Start typing to search Spotify");
                return;
            }

            searchDelay.setOnFinished(e ->
                updateResults(newVal, requestId)
            );

            searchDelay.playFromStart();
        });

        // Initial empty state
        showMessage("Start typing to search Spotify");

        // Add elements to page
        getChildren().addAll(
            title,
            searchField,
            resultsTitle,
            scroll
        );
	}

    // Search through liked songs and playlists
    // Search Spotify's catalog in the background
    private void updateResults(String query, int requestId) {
        resultsContainer.getChildren().clear();

        if (query == null || query.isBlank()) {
            showMessage("Start typing to search Spotify");
            return;
        }

        showMessage("Searching Spotify...");

        Task<List<Track>> searchTask = new Task<>() {
            @Override
            protected List<Track> call() throws Exception {
                return state.spotifyService.searchTracks(query.trim());
            }
        };

        searchTask.setOnSucceeded(event -> {
            if (requestId != searchRequestId) {
                return;
            }

            resultsContainer.getChildren().clear();

            List<Track> matches = searchTask.getValue();

            if (matches.isEmpty()) {
                showMessage("No songs found");
                return;
            }

            int index = 1;

            for (Track track : matches) {
                resultsContainer.getChildren().add(
                    createTrackRow(index++, track)
                );
            }
        });

        searchTask.setOnFailed(event -> {
            if (requestId != searchRequestId) {
                return;
            }

            resultsContainer.getChildren().clear();
            showMessage("Unable to search Spotify. Please try again.");

            searchTask.getException().printStackTrace();
        });

        Thread searchThread = new Thread(searchTask);
        searchThread.setDaemon(true);
        searchThread.start();
    }

    // Display messages inside results container
    private void showMessage(String text) {

        Label msg = new Label(text);
        msg.setTextFill(Color.web("#b3b3b3"));
        msg.setFont(Font.font(14));

        resultsContainer.getChildren().add(msg);
    }

    // Create individual song row
    private HBox createTrackRow(int number, Track track) {

        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-radius: 8px;");
        row.setCursor(Cursor.HAND);
        row.setMaxWidth(Double.MAX_VALUE);

        // Album artwork placeholder
        Label albumArt = new Label("♫");
        albumArt.setPrefSize(50, 50);
        albumArt.setMinSize(50, 50);
        albumArt.setAlignment(Pos.CENTER);
        albumArt.setStyle(
            "-fx-background-color: #282828;" +
            "-fx-text-fill: #1DB954;" +
            "-fx-font-size: 24px;" +
            "-fx-background-radius: 6;"
        );

        // Song information
        VBox info = new VBox(5);

        Label name = new Label(track.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("System", FontWeight.BOLD, 14));
        name.setWrapText(true);

        Label artist = new Label(track.getArtist());
        artist.setTextFill(Color.LIGHTGRAY);
        artist.setFont(Font.font("System", 12));

        info.getChildren().addAll(name, artist);

        // Push play button to the right
        HBox.setHgrow(info, Priority.ALWAYS);

        // Play button
        Button playButton = new Button("▶");
        playButton.setStyle(
            "-fx-background-color: #1DB954;" +
            "-fx-text-fill: black;" +
            "-fx-background-radius: 20;" +
            "-fx-font-size: 12px;" +
            "-fx-cursor: hand;"
        );

        // Play functionality
        Runnable playTrack = () -> {

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

            if (onPlayed != null) {
                onPlayed.run();
            }
        };

        playButton.setOnAction(e -> playTrack.run());

        row.getChildren().addAll(albumArt, info, playButton);

        // Hover effect
        row.setOnMouseEntered(e ->
            row.setStyle(
                "-fx-background-color: #282828;" +
                "-fx-background-radius: 8px;"
            )
        );

        row.setOnMouseExited(e ->
            row.setStyle("-fx-background-radius: 8px;")
        );

        // Clicking the row also plays the song
        row.setOnMouseClicked(e -> {
            if (e.getTarget() != playButton) {
                playTrack.run();
            }
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
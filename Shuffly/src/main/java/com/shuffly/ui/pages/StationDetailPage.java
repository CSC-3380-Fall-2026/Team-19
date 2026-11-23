package com.shuffly.ui.pages;

import com.shuffly.AppState;
import com.shuffly.model.Station;
import com.shuffly.model.Track;
import com.shuffly.spotify.SpotifyApiClient;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StationDetailPage extends VBox {

    private final AppState state;
    private final Runnable onPlaybackStarted;

    public StationDetailPage(
            AppState state,
            Runnable onBack,
            Runnable onPlaybackStarted) {

        this.state = state;
        this.onPlaybackStarted = onPlaybackStarted;

        setSpacing(22);
        setPadding(new Insets(10, 0, 20, 0));
        setFillWidth(true);

        Station station = state.selectedStation;

        // Back button
        Button backBtn = new Button("← Back to Library");
        backBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #b3b3b3;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0 0 8 0;"
        );

        backBtn.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        if (station == null) {
            Label error = new Label("No station selected.");
            error.setTextFill(Color.WHITE);
            getChildren().addAll(backBtn, error);
            return;
        }

        // Station cover
        Label cover = new Label("◉");
        cover.setPrefSize(180, 160);
        cover.setMinSize(180, 160);
        cover.setAlignment(Pos.CENTER);
        cover.setStyle(
            "-fx-background-color: #1e1e2f;" +
            "-fx-text-fill: #1db954;" +
            "-fx-font-size: 65px;" +
            "-fx-background-radius: 12px;"
        );

        // Station name
        Label title = new Label(station.getName());
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 30));
        title.setWrapText(true);

        // Description
        Label description = new Label(station.getDescription());
        description.setTextFill(Color.web("#b3b3b3"));
        description.setFont(Font.font(15));
        description.setWrapText(true);
        description.setMaxWidth(650);

        // Matching tracks
        List<Track> matchingTracks = getMatchingTracks(station);
        Label count = new Label(
            matchingTracks.size() + " matching songs in your library"
        );
        count.setTextFill(Color.web("#b3b3b3"));
        count.setFont(Font.font(13));

        // Play button
        Button playBtn = new Button("▶  Play Station");
        playBtn.setStyle(
            "-fx-background-color: #1db954;" +
            "-fx-text-fill: black;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20px;" +
            "-fx-padding: 12 26;" +
            "-fx-cursor: hand;"
        );

        playBtn.setOnAction(e -> startStation(station, playBtn));

        // Detected moods
        Label moodsTitle = new Label("Detected moods");
        moodsTitle.setTextFill(Color.WHITE);
        moodsTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        HBox moodLabels = new HBox(10);
        moodLabels.setAlignment(Pos.CENTER_LEFT);

        List<String> moods = station.getMatchingMoods();

        if (moods.isEmpty()) {
            Label noMoods = new Label(
                "No moods were detected from this description."
            );
            noMoods.setTextFill(Color.web("#b3b3b3"));
            moodLabels.getChildren().add(noMoods);
        } else {
            for (String mood : moods) {
                Label moodLabel = new Label(
                    mood.substring(0, 1).toUpperCase() + mood.substring(1)
                );

                moodLabel.setTextFill(Color.web("#1db954"));
                moodLabel.setStyle(
                    "-fx-background-color: #282828;" +
                    "-fx-background-radius: 16px;" +
                    "-fx-padding: 7 14;"
                );

                moodLabels.getChildren().add(moodLabel);
            }
        }

        // Explanation
        Label explanationTitle = new Label("How this station works");
        explanationTitle.setTextFill(Color.WHITE);
        explanationTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label explanation = new Label(
            "Shuffly analyzes your station description and identifies " +
            "matching moods. It then finds songs in your Spotify library " +
            "with those mood tags and plays them in a randomized order. " +
            "You won't know which song comes next, just like listening " +
            "to a radio station."
        );

        explanation.setTextFill(Color.web("#b3b3b3"));
        explanation.setFont(Font.font(14));
        explanation.setWrapText(true);
        explanation.setMaxWidth(650);

        getChildren().addAll(
            backBtn,
            cover,
            title,
            description,
            count,
            playBtn,
            moodsTitle,
            moodLabels,
            explanationTitle,
            explanation
        );
    }

    private List<Track> getMatchingTracks(Station station) {

        List<Track> library = getStationLibraryTracks();
        List<Track> matching = new ArrayList<>();

        for (Track track : library) {
            for (String mood : track.getMoodTags()) {
                if (station.getMatchingMoods().contains(
                        mood.toLowerCase())) {
                    matching.add(track);
                    break;
                }
            }
        }

        return matching;
    }

    private List<Track> getStationLibraryTracks() {

        Map<String, Track> uniqueTracks = new LinkedHashMap<>();

        for (Track track : state.likedSongs) {
            if (track != null && track.getId() != null) {
                uniqueTracks.putIfAbsent(track.getId(), track);
            }
        }

        for (com.shuffly.model.Playlist playlist : state.playlists) {
            for (Track track : playlist.getTracks()) {
                if (track != null && track.getId() != null) {
                    uniqueTracks.putIfAbsent(track.getId(), track);
                }
            }
        }

        return new ArrayList<>(uniqueTracks.values());
    }

    private void startStation(Station station, Button playBtn) {

        state.stationPlayer.loadStation(
            station,
            getStationLibraryTracks()
        );

        Track firstTrack = state.stationPlayer.getNextTrack();

        if (firstTrack == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Station Unavailable");
            alert.setHeaderText("No matching songs found");
            alert.setContentText(
                "There are no songs in your library matching this station."
            );
            alert.showAndWait();
            return;
        }

        state.selectedStation = station;
        state.stationMode = true;

        playBtn.setDisable(true);
        playBtn.setText("Starting...");

        new Thread(() -> {
            try {
                SpotifyApiClient api = new SpotifyApiClient();
                String deviceId = api.getActiveDeviceId();

                api.playTracks(
                    List.of(firstTrack.getId()),
                    deviceId
                );

                Platform.runLater(() -> {
                    state.isPlaying = true;
                    playBtn.setDisable(false);
                    playBtn.setText("▶  Play Station");

                    if (onPlaybackStarted != null) {
                        onPlaybackStarted.run();
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();

                Platform.runLater(() -> {
                    playBtn.setDisable(false);
                    playBtn.setText("▶  Play Station");

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Playback Error");
                    alert.setHeaderText("Couldn't start station");
                    alert.setContentText(
                        "Something went wrong while starting Spotify playback."
                    );
                    alert.showAndWait();
                });
            }
        }).start();
    }
}

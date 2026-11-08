package com.shuffly.ui.pages;

import com.shuffly.AppState;
import com.shuffly.model.Playlist;
import com.shuffly.model.Track;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchPage extends VBox {

    private final AppState state;
    private final VBox resultsContainer = new VBox(6);
    private final Runnable onPlayed;

    public SearchPage(AppState state) {
        this(state, null);
    }

    public SearchPage(AppState state, Runnable onPlayed) {
        this.state = state;
        this.onPlayed = onPlayed;

        setSpacing(18);
        setPadding(new Insets(10, 0, 20, 0));
        setStyle("-fx-background-color: transparent;");

        Label title = new Label("Search");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

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

        Label resultsTitle = new Label("Search Results");
        resultsTitle.setTextFill(Color.WHITE);
        resultsTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));
        resultsTitle.setPadding(new Insets(8, 0, 0, 0));

        resultsContainer.setPadding(new Insets(4, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(resultsContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setPrefHeight(450);
        scroll.setPannable(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Live search
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateResults(newVal);
        });

        // Initial empty state
        showMessage("Start typing to search your library");

        getChildren().addAll(title, searchField, resultsTitle, scroll);
    }

    private void updateResults(String query) {
        resultsContainer.getChildren().clear();

        if (query == null || query.isBlank()) {
            showMessage("Start typing to search your library");
            return;
        }

        String q = query.toLowerCase().trim();
        List<Track> matches = new ArrayList<>();

        // Deduplicate by track id
        Map<String, Track> found = new LinkedHashMap<>();

        // Liked songs
        for (Track track : state.likedSongs) {
            if (matchesQuery(track, q)) {
                found.put(track.getId(), track);
            }
        }

        // All loaded playlists
        for (Playlist playlist : state.playlists) {
            for (Track track : playlist.getTracks()) {
                if (matchesQuery(track, q)) {
                    found.put(track.getId(), track);
                }
            }
        }

        matches.addAll(found.values());

        if (matches.isEmpty()) {
            showMessage("No songs found");
            return;
        }

        int index = 1;
        for (Track track : matches) {
            resultsContainer.getChildren().add(createTrackRow(index++, track));
        }
    }

    private boolean matchesQuery(Track track, String q) {
        return track.getName().toLowerCase().contains(q)
            || track.getArtist().toLowerCase().contains(q);
    }

    private void showMessage(String text) {
        Label msg = new Label(text);
        msg.setTextFill(Color.web("#b3b3b3"));
        msg.setFont(Font.font(14));
        resultsContainer.getChildren().add(msg);
    }

    private HBox createTrackRow(int number, Track track) {
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
            state.queueManager.loadPlaylist(java.util.List.of(track), false);
            state.isPlaying = true;
            if (onPlayed != null) {
                onPlayed.run();
            }
        });

        return row;
    }
}
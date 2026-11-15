
package com.shuffly.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class SearchPage extends VBox {

    private List<String> songs = new ArrayList<>();

    public SearchPage() {

        // Layout settings
        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_LEFT);
        setStyle("-fx-background-color: #121212;");

        // Sample songs for testing
        songs.add("Snooze - SZA");
        songs.add("Good Days - SZA");
        songs.add("Pink + White - Frank Ocean");
        songs.add("Superstar - Usher");
        songs.add("Focus - H.E.R.");

        // Page title
        Label title = new Label("Search");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search songs or artists...");
        searchField.setPrefWidth(400);
        searchField.setMaxWidth(500);
        searchField.setStyle(
            "-fx-background-color: #282828;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #aaaaaa;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;"
        );

        // Results heading
        Label resultsTitle = new Label("Search Results");
        resultsTitle.setTextFill(Color.WHITE);
        resultsTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Results container
        VBox resultsContainer = new VBox(8);
        resultsContainer.setPadding(new Insets(10, 0, 0, 0));

        // Display all songs initially
        for (String song : songs) {
            resultsContainer.getChildren().add(createSongRow(song));
        }

        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            resultsContainer.getChildren().clear();

            for (String song : songs) {

                if (song.toLowerCase().contains(newValue.toLowerCase())) {
                    resultsContainer.getChildren().add(createSongRow(song));
                }
            }

            // No results message
            if (resultsContainer.getChildren().isEmpty() && !newValue.isBlank()) {

                Label noResults = new Label("No songs found.");
                noResults.setTextFill(Color.LIGHTGRAY);
                noResults.setFont(Font.font("System", 14));

                resultsContainer.getChildren().add(noResults);
            }
        });

        // Add elements to page
        getChildren().addAll(title, searchField, resultsTitle, resultsContainer);
    }

    // Create individual song row
    private HBox createSongRow(String song) {

        HBox songRow = new HBox(15);
        songRow.setAlignment(Pos.CENTER_LEFT);
        songRow.setPadding(new Insets(10));
        songRow.setStyle("-fx-background-color: transparent;");

        // Separate song title and artist
        String[] songInfo = song.split(" - ");

        String songTitle = songInfo[0];
        String artist = songInfo.length > 1 ? songInfo[1] : "Unknown Artist";

        // Album artwork placeholder
        Label albumArt = new Label("♫");
        albumArt.setPrefSize(50, 50);
        albumArt.setAlignment(Pos.CENTER);
        albumArt.setStyle(
            "-fx-background-color: #282828;" +
            "-fx-text-fill: #1DB954;" +
            "-fx-font-size: 24px;" +
            "-fx-background-radius: 6;"
        );

        // Song information
        VBox songDetails = new VBox(5);

        Label titleLabel = new Label(songTitle);
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label artistLabel = new Label(artist);
        artistLabel.setTextFill(Color.LIGHTGRAY);
        artistLabel.setFont(Font.font("System", 12));

        songDetails.getChildren().addAll(titleLabel, artistLabel);

        // Play button
        Button playButton = new Button("▶");
        playButton.setStyle(
            "-fx-background-color: #1DB954;" +
            "-fx-text-fill: black;" +
            "-fx-background-radius: 20;" +
            "-fx-font-size: 12px;" +
            "-fx-cursor: hand;"
        );

        // Push play button to the right
        songDetails.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(songDetails, javafx.scene.layout.Priority.ALWAYS);

        songRow.getChildren().addAll(albumArt, songDetails, playButton);

        // Hover effect
        songRow.setOnMouseEntered(event ->
            songRow.setStyle("-fx-background-color: #282828; -fx-background-radius: 8;")
        );

        songRow.setOnMouseExited(event ->
            songRow.setStyle("-fx-background-color: transparent;")
        );

        return songRow;
    }
}

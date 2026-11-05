
package com.shuffly.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

        // Results container
        VBox resultsContainer = new VBox(10);
        resultsContainer.setPadding(new Insets(10, 0, 0, 0));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

        // Clear previous search results
        resultsContainer.getChildren().clear();

        // Check each song
        for (String song : songs) {

          if (song.toLowerCase().contains(newValue.toLowerCase())) {

            Label songLabel = new Label(song);
            songLabel.setTextFill(Color.WHITE);

            resultsContainer.getChildren().add(songLabel);
            }
        }

        // Show message if no songs match
        if (resultsContainer.getChildren().isEmpty() && !newValue.isBlank()) {
          Label noResults = new Label("No songs found.");
          noResults.setTextFill(Color.LIGHTGRAY);
          resultsContainer.getChildren().add(noResults);
        }
    });

        // Results heading
        Label resultsTitle = new Label("Search Results");
        resultsTitle.setTextFill(Color.WHITE);
        resultsTitle.setFont(Font.font("System", 18));

        // Add elements to the page
        getChildren().addAll(title, searchField, resultsTitle, resultsContainer);
    }
}

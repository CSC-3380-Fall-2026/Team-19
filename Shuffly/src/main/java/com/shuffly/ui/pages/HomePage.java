package com.shuffly.ui.pages;

import com.shuffly.AppState;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.shuffly.spotify.*;

public class HomePage extends VBox {

    private final AppState state;

    public HomePage(AppState state) {
        this.state = state;

        setSpacing(25);
        setPadding(new Insets(10, 0, 20, 0));

        Label welcome = new Label("Welcome to Shuffly");
        welcome.setTextFill(Color.WHITE);
        welcome.setFont(Font.font("System", FontWeight.BOLD, 28));

        Label subtitle = new Label("Connect your Spotify account to start using smarter shuffle modes; Requires Spotify Premium");
        subtitle.setTextFill(Color.web("#b3b3b3"));
        subtitle.setFont(Font.font(15));
        subtitle.setPadding(new Insets(0, 0, 10, 0));

        VBox connectionCard = new VBox(18);
        connectionCard.setPadding(new Insets(28));
        connectionCard.setStyle("-fx-background-color: #181818; -fx-background-radius: 12px;");
        connectionCard.setMaxWidth(480);

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font(15));

        Button connectBtn = new Button();
        connectBtn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        connectBtn.setCursor(javafx.scene.Cursor.HAND);

        updateConnectionUI(statusLabel, connectBtn);

        connectBtn.setOnAction(e -> {
            if (!state.spotifyConnected) {
                // Run on a background thread so the UI doesn't freeze
                new Thread(() -> {
                    try {
                        String token = SpotifyAuth.login();
                        if (token != null) {
                            SpotifyApiClient api = new SpotifyApiClient();
                            String name = api.getCurrentUserName();

                            javafx.application.Platform.runLater(() -> {
                                state.spotifyConnected = true;
                                state.spotifyUserName = name != null ? name : "Spotify User";
                                updateConnectionUI(statusLabel, connectBtn);
                            });
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            } else {
                SpotifyAuth.logout();
                state.spotifyConnected = false;
                state.spotifyUserName = null;
                updateConnectionUI(statusLabel, connectBtn);
            }
        });

        connectionCard.getChildren().addAll(statusLabel, connectBtn);
        getChildren().addAll(welcome, subtitle, connectionCard);
    }

    private void updateConnectionUI(Label statusLabel, Button connectBtn) {
        if (state.spotifyConnected) {
            statusLabel.setText("Connected as " + state.spotifyUserName);
            statusLabel.setTextFill(Color.web("#1db954"));

            connectBtn.setText("Disconnect");
            connectBtn.setStyle(
                "-fx-background-color: #282828; -fx-text-fill: white; " +
                "-fx-background-radius: 20px; -fx-padding: 12 28; -fx-cursor: hand;"
            );
        } else {
            statusLabel.setText("Not connected to Spotify");
            statusLabel.setTextFill(Color.web("#b3b3b3"));

            connectBtn.setText("Connect to Spotify");
            connectBtn.setStyle(
                "-fx-background-color: #1db954; -fx-text-fill: black; " +
                "-fx-background-radius: 20px; -fx-padding: 12 28; -fx-cursor: hand;"
            );
        }
    }
}
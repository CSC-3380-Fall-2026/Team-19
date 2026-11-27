package com.shuffly.ui.pages;

import com.shuffly.AppState;
import com.shuffly.model.Playlist;
import com.shuffly.model.Station;
import com.shuffly.spotify.SpotifyService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.Alert;

public class LibraryPage extends VBox {

    private final AppState state;
    private final Consumer<Playlist> onPlaylistSelected;
    private final Consumer<Station> onStationSelected;

    public LibraryPage(AppState state, Consumer<Playlist> onPlaylistSelected, Consumer<Station> onStationSelected) {

        this.state = state;
        this.onPlaylistSelected = onPlaylistSelected;
        this.onStationSelected = onStationSelected;

        setSpacing(35);
        setPadding(new Insets(10, 0, 20, 0));

        rebuildLibraryContent();
    }

    private void rebuildLibraryContent() {
        getChildren().clear();

        List<Playlist> playlists = state.playlists;

        // ===== PLAYLISTS =====
        VBox playlistsSection = new VBox(16);

        HBox libraryHeader = new HBox(16);
        libraryHeader.setAlignment(Pos.CENTER_LEFT);

        Label playlistsTitle = new Label("Playlists");
        playlistsTitle.setTextFill(Color.WHITE);
        playlistsTitle.setFont(Font.font("System", FontWeight.BOLD, 22));

        Button refreshButton = new Button("↻ Refresh Library");
        refreshButton.setStyle(
            "-fx-background-color: #282828;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20px;" +
            "-fx-font-size: 12px;" +
            "-fx-cursor: hand;"
        );
        refreshButton.setOnAction(e -> refreshLibrary(refreshButton));

        libraryHeader.getChildren().addAll(playlistsTitle, refreshButton);

        HBox playlistCards = new HBox(18);
        playlistCards.setAlignment(Pos.CENTER_LEFT);
        playlistCards.setPadding(new Insets(0, 0, 10, 0));

        for (Playlist playlist : playlists) {
            playlistCards.getChildren().add(createPlaylistCard(playlist));
        }

        ScrollPane playlistScroll = new ScrollPane();
        playlistScroll.setContent(playlistCards);
        playlistScroll.setFitToHeight(true);
        playlistScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        playlistScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        playlistScroll.setPannable(true);
        playlistScroll.setPrefHeight(230);
        playlistScroll.setMinHeight(230);
        playlistScroll.setMaxWidth(Double.MAX_VALUE);
        playlistScroll.getStyleClass().add("library-scroll");
        applyAccentToScrollPane(playlistScroll, state.accentColor);

        playlistsSection.getChildren().addAll(libraryHeader, playlistScroll);

        // ===== STATIONS =====
        VBox stationsSection = new VBox(16);

        HBox stationsHeader = new HBox(16);
        stationsHeader.setAlignment(Pos.CENTER_LEFT);

        Label stationsTitle = new Label("Stations");
        stationsTitle.setTextFill(Color.WHITE);
        stationsTitle.setFont(Font.font("System", FontWeight.BOLD, 22));

        Label createStationButton = new Label("+ Create Station");
        createStationButton.setTextFill(state.accentColor);
        createStationButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        createStationButton.setCursor(javafx.scene.Cursor.HAND);
        createStationButton.setOnMouseClicked(e -> showCreateStationDialog());

        stationsHeader.getChildren().addAll(stationsTitle, createStationButton);

        HBox stationCards = new HBox(18);
        stationCards.setAlignment(Pos.CENTER_LEFT);
        stationCards.setPadding(new Insets(0, 0, 10, 0));

        for (Station station : state.stations) {
            stationCards.getChildren().add(createStationCard(station));
        }

        ScrollPane stationScroll = new ScrollPane();
        stationScroll.setContent(stationCards);
        stationScroll.setFitToHeight(true);
        stationScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        stationScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        stationScroll.setPannable(true);
        stationScroll.setPrefHeight(190);
        stationScroll.setMinHeight(190);
        stationScroll.setMaxWidth(Double.MAX_VALUE);
        stationScroll.getStyleClass().add("library-scroll");
        applyAccentToScrollPane(stationScroll, state.accentColor);

        stationsSection.getChildren().addAll(stationsHeader, stationScroll);

        getChildren().addAll(playlistsSection, stationsSection);
    }
    
    private void refreshLibrary(Button refreshButton) {
        refreshButton.setDisable(true);
        refreshButton.setText("Refreshing...");

        Thread refreshThread = new Thread(() -> {
            try {
                new SpotifyService().refreshUserLibrary(state);

                Platform.runLater(() -> {
                    rebuildLibraryContent();
                });

            } catch (Exception ex) {
                ex.printStackTrace();

                Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    refreshButton.setText("↻ Refresh Library");

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Library Refresh Failed");
                    alert.setHeaderText("Could not refresh your Spotify library.");
                    alert.setContentText(
                        "Please check your Spotify connection and try again."
                    );
                    alert.showAndWait();
                });
            }
        });

        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    private VBox createPlaylistCard(Playlist playlist) {
        VBox card = new VBox(10);
        card.setPrefWidth(160);
        card.setMinWidth(160);
        card.setMaxWidth(160);
        card.setCursor(javafx.scene.Cursor.HAND);

        Label cover = new Label("♪");
        cover.setPrefSize(160, 160);
        cover.setMinSize(160, 160);
        cover.setMaxSize(160, 160);
        cover.setAlignment(Pos.CENTER);
        cover.setStyle(
            "-fx-background-color: #282828;" +
            "-fx-text-fill: #b3b3b3;" +
            "-fx-font-size: 42px;" +
            "-fx-background-radius: 8px;"
        );

        Label name = new Label(playlist.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        name.setMaxWidth(160);
        name.setWrapText(true);

        Label info = new Label(playlist.getTrackCount() + " songs");
        info.setTextFill(Color.web("#b3b3b3"));
        info.setFont(Font.font(12));

        card.getChildren().addAll(cover, name, info);

        card.setOnMouseEntered(e -> cover.setStyle(
            "-fx-background-color: #333333;" +
            "-fx-text-fill: #b3b3b3;" +
            "-fx-font-size: 42px;" +
            "-fx-background-radius: 8px;"
        ));
        card.setOnMouseExited(e -> cover.setStyle(
            "-fx-background-color: #282828;" +
            "-fx-text-fill: #b3b3b3;" +
            "-fx-font-size: 42px;" +
            "-fx-background-radius: 8px;"
        ));

        card.setOnMouseClicked(e -> {
            new Thread(() -> {
                try {
                    if (playlist.getTracks().isEmpty()
                            && state.spotifyConnected
                            && !playlist.getId().startsWith("local-")) {
                        new SpotifyService().loadPlaylistTracks(playlist);
                    }

                    Platform.runLater(() -> {
                    	state.selectedPlaylist = playlist;

                        if (onPlaylistSelected != null) {
                            onPlaylistSelected.accept(playlist);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        return card;
    }

    private VBox createStationCard(Station station) {
        VBox card = new VBox(10);
        card.setPrefWidth(180);
        card.setMinWidth(180);
        card.setMaxWidth(180);
        card.setCursor(javafx.scene.Cursor.HAND);

        Label cover = new Label("◉");
        cover.setPrefSize(180, 120);
        cover.setMinSize(180, 120);
        cover.setMaxSize(180, 120);
        cover.setAlignment(Pos.CENTER);
        cover.setStyle(
            "-fx-background-color: #1e1e2f;" +
            "-fx-text-fill: #1db954;" +
            "-fx-font-size: 36px;" +
            "-fx-background-radius: 8px;"
        );

        Label name = new Label(station.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

        Label info = new Label(station.getDescription());
        info.setTextFill(Color.web("#b3b3b3"));
        info.setFont(Font.font(12));
        info.setWrapText(true);
        info.setMaxWidth(180);

        card.getChildren().addAll(cover, name, info);
        card.setOnMouseClicked(e -> onStationSelected.accept(station));

        card.setOnMouseEntered(e -> cover.setStyle(
            "-fx-background-color: #2a2a3d;" +
            "-fx-text-fill: #1db954;" +
            "-fx-font-size: 36px;" +
            "-fx-background-radius: 8px;"
        ));
        card.setOnMouseExited(e -> cover.setStyle(
            "-fx-background-color: #1e1e2f;" +
            "-fx-text-fill: #1db954;" +
            "-fx-font-size: 36px;" +
            "-fx-background-radius: 8px;"
        ));

        return card;
    }
    
    private void showCreateStationDialog() {
        Dialog<Station> dialog = new Dialog<>();

        dialog.setTitle("Create Station");
        dialog.setHeaderText("Create your own listening station");

        TextField nameField = new TextField();
        nameField.setPromptText("Station name");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Describe your station");
        descriptionField.setPrefRowCount(3);

        VBox content = new VBox(12);
        content.getChildren().addAll(
            new Label("Station Name"),
            nameField,
            new Label("Description"),
            descriptionField
        );

        dialog.getDialogPane().setContent(content);

        ButtonType createButtonType = new ButtonType(
            "Create",
            javafx.scene.control.ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
            createButtonType,
            ButtonType.CANCEL
        );

        dialog.setResultConverter(button -> {
            if (button == createButtonType) {
                String name = nameField.getText().trim();
                String description = descriptionField.getText().trim();

                if (!name.isEmpty()) {
                    return new Station(
                        java.util.UUID.randomUUID().toString(),
                        name,
                        description,
                        null
                    );
                }
            }

            return null;
        });

        dialog.showAndWait().ifPresent(station -> {
            state.stations.add(station);
            rebuildLibraryContent();
        });
    }
        
    private void applyAccentToScrollPane(ScrollPane scroll, Color accent) {
        String hex = String.format("#%02X%02X%02X",
            (int) (accent.getRed() * 255),
            (int) (accent.getGreen() * 255),
            (int) (accent.getBlue() * 255)
        );

        scroll.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background: transparent;"
        );

        scroll.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                styleScrollThumb(scroll, hex);
            }
        });

        styleScrollThumb(scroll, hex);
    }

    private void styleScrollThumb(ScrollPane scroll, String accentHex) {
        javafx.scene.Node thumb = scroll.lookup(".thumb");
        javafx.scene.Node track = scroll.lookup(".track");

        if (track != null) {
            track.setStyle(
                "-fx-background-color: rgba(255,255,255,0.08);" +
                "-fx-background-radius: 4px;"
            );
        }

        if (thumb != null) {
            thumb.setStyle(
                "-fx-background-color: " + accentHex + ";" +
                "-fx-background-radius: 4px;"
            );
        }
    }
}
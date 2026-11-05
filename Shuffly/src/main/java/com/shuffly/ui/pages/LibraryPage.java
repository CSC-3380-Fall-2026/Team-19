package com.shuffly.ui.pages;

import com.shuffly.AppState;
import com.shuffly.model.Playlist;
import com.shuffly.model.Station;
import com.shuffly.model.Track;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LibraryPage extends VBox {

    private final AppState state;
    private final Consumer<Playlist> onPlaylistSelected;

    public LibraryPage(AppState state, Consumer<Playlist> onPlaylistSelected) {
        this.state = state;
        this.onPlaylistSelected = onPlaylistSelected;

        setSpacing(35);
        setPadding(new Insets(10, 0, 20, 0));

        List<Playlist> playlists = state.spotifyConnected && !state.playlists.isEmpty()
            ? state.playlists
            : createMockPlaylists();

     // ===== PLAYLISTS =====
        VBox playlistsSection = new VBox(16);
        Label playlistsTitle = new Label("Playlists");
        playlistsTitle.setTextFill(Color.WHITE);
        playlistsTitle.setFont(Font.font("System", FontWeight.BOLD, 22));

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
        HBox.setHgrow(playlistScroll, Priority.ALWAYS);

        // Important: keep background transparent but don't wipe out content
        playlistScroll.getStyleClass().add("library-scroll");
        playlistScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        applyAccentToScrollPane(playlistScroll, state.accentColor);

        playlistsSection.getChildren().addAll(playlistsTitle, playlistScroll);

        // ===== STATIONS =====
        VBox stationsSection = new VBox(16);
        Label stationsTitle = new Label("Stations");
        stationsTitle.setTextFill(Color.WHITE);
        stationsTitle.setFont(Font.font("System", FontWeight.BOLD, 22));

        HBox stationCards = new HBox(18);
        stationCards.setAlignment(Pos.CENTER_LEFT);
        stationCards.setPadding(new Insets(0, 0, 10, 0));

        for (Station station : createMockStations()) {
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
        stationScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        applyAccentToScrollPane(stationScroll, state.accentColor);
        
        stationsSection.getChildren().addAll(stationsTitle, stationScroll);

        getChildren().addAll(playlistsSection, stationsSection);
    }
    
    private VBox createPlaylistCard(Playlist playlist) {
        VBox card = new VBox(10);
        card.setPrefWidth(160);
        card.setMinWidth(160);
        card.setMaxWidth(160);
        card.setCursor(javafx.scene.Cursor.HAND);

        // Cover art placeholder
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

        // Hover effect
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

        // Click → load tracks if needed, put in queue, open playlist page
        card.setOnMouseClicked(e -> {
            new Thread(() -> {
                try {
                    if (playlist.getTracks().isEmpty() && state.spotifyConnected) {
                        new com.shuffly.spotify.SpotifyService().loadPlaylistTracks(playlist);
                    }

                    javafx.application.Platform.runLater(() -> {
                        state.queueManager.loadPlaylist(playlist.getTracks(), true);
                        state.isPlaying = true;
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
        cover.setAlignment(Pos.CENTER);
        cover.setStyle(
            "-fx-background-color: #1e1e2f; -fx-text-fill: #1db954; " +
            "-fx-font-size: 36px; -fx-background-radius: 8px;"
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

        card.setOnMouseEntered(e -> cover.setStyle(
            "-fx-background-color: #2a2a3d; -fx-text-fill: #1db954; " +
            "-fx-font-size: 36px; -fx-background-radius: 8px;"
        ));
        card.setOnMouseExited(e -> cover.setStyle(
            "-fx-background-color: #1e1e2f; -fx-text-fill: #1db954; " +
            "-fx-font-size: 36px; -fx-background-radius: 8px;"
        ));

        return card;
    }
    
    private void applyAccentToScrollPane(ScrollPane scroll, Color accent) {
        String hex = String.format("#%02X%02X%02X",
            (int) (accent.getRed() * 255),
            (int) (accent.getGreen() * 255),
            (int) (accent.getBlue() * 255)
        );

        // Thin track + accent thumb
        scroll.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background: transparent;"
        );

        // Style the thumb after the skin exists
        scroll.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                styleScrollThumb(scroll, hex);
            }
        });

        // Also try immediately in case skin is already there
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

    // Temporary mock data – replace with Spotify data later
    private List<Playlist> createMockPlaylists() {
        List<Playlist> list = new ArrayList<>();

        Playlist liked = new Playlist("1", "Liked Songs", null);
        liked.addTrack(new Track("t1", "Blinding Lights", "The Weeknd", null, 200000));
        liked.addTrack(new Track("t2", "Save Your Tears", "The Weeknd", null, 215000));
        liked.addTrack(new Track("t3", "Starboy", "The Weeknd", null, 230000));
        liked.addTrack(new Track("t4", "Die For You", "The Weeknd", null, 260000));
        list.add(liked);

        list.add(new Playlist("2", "Chill Mix", null));
        list.add(new Playlist("3", "Focus Flow", null));
        list.add(new Playlist("4", "Late Night", null));
        list.add(new Playlist("5", "Workout", null));

        return list;
    }

    private List<Station> createMockStations() {
        List<Station> list = new ArrayList<>();
        list.add(new Station("s1", "Mood: Chill", "Based on your calm listening", null));
        list.add(new Station("s2", "Artist Radio", "Similar to your top artists", null));
        list.add(new Station("s3", "Discovery", "New music you might like", null));
        list.add(new Station("s4", "Energy Boost", "High energy tracks", null));
        return list;
    }
}
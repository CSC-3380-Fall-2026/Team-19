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

import java.util.ArrayList;
import java.util.List;

public class LibraryPage extends VBox {

    private final AppState state;
    private final Runnable onPlaylistLoaded;

    public LibraryPage(AppState state, Runnable onPlaylistLoaded) {
        this.state = state;
        this.onPlaylistLoaded = onPlaylistLoaded;

        setSpacing(35);
        setPadding(new Insets(10, 0, 20, 0));

        // ===== PLAYLISTS =====
        VBox playlistsSection = new VBox(16);
        Label playlistsTitle = new Label("Playlists");
        playlistsTitle.setTextFill(Color.WHITE);
        playlistsTitle.setFont(Font.font("System", FontWeight.BOLD, 22));

        HBox playlistCards = new HBox(18);
        for (Playlist playlist : createMockPlaylists()) {
            playlistCards.getChildren().add(createPlaylistCard(playlist));
        }
        playlistsSection.getChildren().addAll(playlistsTitle, playlistCards);

        // ===== STATIONS =====
        VBox stationsSection = new VBox(16);
        Label stationsTitle = new Label("Stations");
        stationsTitle.setTextFill(Color.WHITE);
        stationsTitle.setFont(Font.font("System", FontWeight.BOLD, 22));

        HBox stationCards = new HBox(18);
        for (Station station : createMockStations()) {
            stationCards.getChildren().add(createStationCard(station));
        }
        stationsSection.getChildren().addAll(stationsTitle, stationCards);

        getChildren().addAll(playlistsSection, stationsSection);
    }

    private VBox createPlaylistCard(Playlist playlist) {
        VBox card = new VBox(10);
        card.setPrefWidth(160);
        card.setCursor(javafx.scene.Cursor.HAND);

        Label cover = new Label("♪");
        cover.setPrefSize(160, 160);
        cover.setAlignment(Pos.CENTER);
        cover.setStyle(
            "-fx-background-color: #282828; -fx-text-fill: #b3b3b3; " +
            "-fx-font-size: 42px; -fx-background-radius: 8px;"
        );

        Label name = new Label(playlist.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

        Label info = new Label(playlist.getTrackCount() + " songs");
        info.setTextFill(Color.web("#b3b3b3"));
        info.setFont(Font.font(12));

        card.getChildren().addAll(cover, name, info);

        card.setOnMouseEntered(e -> cover.setStyle(
            "-fx-background-color: #333333; -fx-text-fill: #b3b3b3; " +
            "-fx-font-size: 42px; -fx-background-radius: 8px;"
        ));
        card.setOnMouseExited(e -> cover.setStyle(
            "-fx-background-color: #282828; -fx-text-fill: #b3b3b3; " +
            "-fx-font-size: 42px; -fx-background-radius: 8px;"
        ));

        card.setOnMouseClicked(e -> {
            state.queueManager.loadPlaylist(playlist.getTracks(), true);
            state.isPlaying = true;
            if (onPlaylistLoaded != null) {
                onPlaylistLoaded.run();
            }
        });

        return card;
    }

    private VBox createStationCard(Station station) {
        VBox card = new VBox(10);
        card.setPrefWidth(180);
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
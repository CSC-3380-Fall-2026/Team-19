package com.shuffly;

import com.shuffly.model.Track;
import com.shuffly.model.Playlist;
import javafx.scene.paint.Color;
import com.shuffly.model.Station;
import com.shuffly.shuffle.StationPlayer;
import com.shuffly.spotify.SpotifyService;

import java.util.ArrayList;
import java.util.List;

public class AppState {
    public final QueueManager<Track> queueManager = new QueueManager<>();

    public final SpotifyService spotifyService = new SpotifyService();
    
    public boolean isPlaying = false;
    public boolean spotifyConnected = false;
    public String spotifyUserName = null;
    public Color accentColor = Color.web("#1db954");
    public String currentShuffleMode = "Truly Random";
    public Playlist selectedPlaylist = null;
    public String selectedMood = "Chill"; // default
    
    public List<Playlist> playlists = new ArrayList<>();
    public List<Track> likedSongs = new ArrayList<>();
    
    public List<Station> stations = new ArrayList<>();
    public Station selectedStation = null;
    public StationPlayer stationPlayer = new StationPlayer();
    public boolean stationMode = false;

   
    
    public Track findTrackById(String trackId) {
        if (trackId == null || trackId.isBlank()) {
            return null;
        }

        // Search liked songs.
        for (Track track : likedSongs) {
            if (track.getId().equals(trackId)) {
                return track;
            }
        }

        // Search all loaded playlists.
        for (Playlist playlist : playlists) {
            for (Track track : playlist.getTracks()) {
                if (track.getId().equals(trackId)) {
                    return track;
                }
            }
        }

        // Search the selected playlist, in case it isn't in the main list.
        if (selectedPlaylist != null) {
            for (Track track : selectedPlaylist.getTracks()) {
                if (track.getId().equals(trackId)) {
                    return track;
                }
            }
        }

        // Search the current queue.
        Track current = queueManager.getCurrent();
        if (current != null && current.getId().equals(trackId)) {
            return current;
        }

        for (Track track : queueManager.getUpNext()) {
            if (track.getId().equals(trackId)) {
                return track;
            }
        }

        return null;
    }
}
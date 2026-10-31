package com.shuffly;

import com.shuffly.model.Track;
import com.shuffly.model.Playlist;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class AppState {
    public final QueueManager<Track> queueManager = new QueueManager<>();
    
    public boolean isPlaying = false;
    public boolean spotifyConnected = false;
    public String spotifyUserName = null;
    public Color accentColor = Color.web("#1db954");
    public String currentShuffleMode = "Mood-based";
    public Playlist selectedPlaylist = null;
    
    public List<Playlist> playlists = new ArrayList<>();
    public List<Track> likedSongs = new ArrayList<>();
}


package com.shuffly;

import com.shuffly.model.Track;
import javafx.scene.paint.Color;

public class AppState {
    public final QueueManager<Track> queueManager = new QueueManager<>();
    
    public boolean isPlaying = false;
    public boolean spotifyConnected = false;
    public String spotifyUserName = null;
    public Color accentColor = Color.web("#1db954");
    public String currentShuffleMode = "Mood-based";
}
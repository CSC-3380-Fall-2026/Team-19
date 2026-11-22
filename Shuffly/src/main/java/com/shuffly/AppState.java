package com.shuffly;

import com.shuffly.model.Track;
import com.shuffly.model.Playlist;
import javafx.scene.paint.Color;
import com.shuffly.model.Station;
import com.shuffly.model.Station;
import com.shuffly.shuffle.StationPlayer;

import java.util.ArrayList;
import java.util.List;

public class AppState {
    public final QueueManager<Track> queueManager = new QueueManager<>();
    
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
    
}


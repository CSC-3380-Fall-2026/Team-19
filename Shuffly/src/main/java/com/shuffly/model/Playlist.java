package com.shuffly.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private final String id;
    private final String name;
    private final String imageUrl;
    private final List<Track> tracks;

    public Playlist(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.tracks = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public List<Track> getTracks() { return tracks; }

    public int getTrackCount() {
        return tracks.size();
    }

    public void addTrack(Track track) {
        tracks.add(track);
    }
}
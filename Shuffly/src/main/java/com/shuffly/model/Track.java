package com.shuffly.model;

public class Track {
    private final String id;
    private final String name;
    private final String artist;
    private final String albumArtUrl;
    private final int durationMs;

    public Track(String id, String name, String artist, String albumArtUrl, int durationMs) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.albumArtUrl = albumArtUrl;
        this.durationMs = durationMs;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getArtist() { return artist; }
    public String getAlbumArtUrl() { return albumArtUrl; }
    public int getDurationMs() { return durationMs; }
}
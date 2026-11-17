package com.shuffly.model;

import java.util.ArrayList;
import java.util.List;

public class Track {
    private final String id;
    private final String name;
    private final String artist;
    private final String albumArtUrl;
    private final int durationMs;

    // New mood-related fields
    private int popularity = 0;              // 0–100 from Spotify
    private final List<String> genres = new ArrayList<>(); // from artist
    private String artistId = null;          // needed to fetch genres

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

    public int getPopularity() { return popularity; }
    public void setPopularity(int popularity) { this.popularity = popularity; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) {
        this.genres.clear();
        if (genres != null) this.genres.addAll(genres);
    }

    public String getArtistId() { return artistId; }
    public void setArtistId(String artistId) { this.artistId = artistId; }

    @Override
    public String toString() {
        return name + " – " + artist;
    }
}
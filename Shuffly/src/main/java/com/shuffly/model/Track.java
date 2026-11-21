
package com.shuffly.model;

import java.util.ArrayList;
import java.util.List;

public class Track {

    private final String id;
    private final String name;
    private final String artist;
    private final String albumArtUrl;
    private final int durationMs;

    // Mood tags assigned automatically or customized by the user
    private final List<String> moodTags = new ArrayList<>();
    private boolean moodTagsCustomized = false;

    public Track(String id, String name, String artist,
                 String albumArtUrl, int durationMs) {

        this.id = id;
        this.name = name;
        this.artist = artist;
        this.albumArtUrl = albumArtUrl;
        this.durationMs = durationMs;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumArtUrl() {
        return albumArtUrl;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public List<String> getMoodTags() {
        return moodTags;
    }

    public void setMoodTags(List<String> tags) {
        moodTags.clear();

        if (tags != null) {
            for (String tag : tags) {
                if (tag != null && !tag.isBlank()) {
                    moodTags.add(tag.toLowerCase().trim());
                }
            }
        }
    }

    public boolean areMoodTagsCustomized() {
        return moodTagsCustomized;
    }

    public void setMoodTagsCustomized(boolean customized) {
        this.moodTagsCustomized = customized;
    }
}


package com.shuffly.model;

import java.util.ArrayList;
import java.util.List;

import com.shuffly.shuffle.StationMoodMatcher;

public class Station {

    private final String id;
    private final String name;
    private final String description;
    private final String imageUrl;

    private final List<String> matchingMoods = new ArrayList<>();

    public Station(String id, String name, String description, String imageUrl) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;

        // Determine moods from the station description
        matchingMoods.addAll(
            StationMoodMatcher.getMoodsFromDescription(description)
        );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getMatchingMoods() {
        return new ArrayList<>(matchingMoods);
    }
}

package com.shuffly.model;

public class Station {
    private final String id;
    private final String name;
    private final String description;
    private final String imageUrl;

    public Station(String id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
}
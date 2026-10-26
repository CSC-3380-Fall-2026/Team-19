package com.shuffly;

public enum ShuffleMode {
    MOOD_BASED("Mood-based", new TrulyRandomShuffle()),         // placeholder 
    TRULY_RANDOM("Truly Random", new TrulyRandomShuffle()),
    ANTI_POPULARITY("Anti-Popularity", new TrulyRandomShuffle()), // placeholder 
    ARTIST_SPACING("Artist Spacing", new ArtistSpacingShuffle()),
    DISCOVERY("Discovery", new AvoidRecentShuffle());           // placeholder 

    private final String label;
    private final ShuffleStrategy strategy;

    ShuffleMode(String label, ShuffleStrategy strategy) {
        this.label = label;
        this.strategy = strategy;
    }

    public String getLabel()             { return label; }
    public ShuffleStrategy getStrategy() { return strategy; }
}

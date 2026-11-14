package com.shuffly.shuffle;

import com.shuffly.model.Track;
import java.util.*;

public class MoodBasedShuffle implements ShuffleStrategy {

    private final String mood;

    public MoodBasedShuffle(String mood) {
        this.mood = mood == null ? "chill" : mood.toLowerCase();
    }

    // genre keywords per mood
    private static final Map<String, List<String>> MOOD_GENRES = Map.of(
        "happy", List.of("pop", "dance", "indie pop", "funk", "disco"),
        "chill", List.of("ambient", "lo-fi", "chill", "acoustic", "indie", "soft"),
        "sad", List.of("sad", "emo", "singer-songwriter", "alternative", "soul"),
        "energetic", List.of("edm", "rock", "hip hop", "pop rap", "metal", "punk"),
        "focus", List.of("ambient", "classical", "lo-fi", "instrumental", "study"),
        "party", List.of("dance", "edm", "pop", "hip hop", "reggaeton", "house"),
        "romantic", List.of("r&b", "soul", "pop", "indie", "jazz"),
        "angry", List.of("metal", "punk", "hard rock", "rap", "industrial")
    );

    @Override
    public List<Track> shuffle(List<Track> songs, List<Track> recentlyPlayed) {
        List<Track> scored = new ArrayList<>(songs);

        scored.sort((a, b) -> Double.compare(score(b), score(a))); // high score first

        // light shuffle within tiers so it doesn't feel rigid
        return softShuffleByScore(scored);
    }

    private double score(Track t) {
        double score = 0;
        List<String> keywords = MOOD_GENRES.getOrDefault(normalizeMood(mood), MOOD_GENRES.get("chill"));

        for (String genre : t.getGenres()) {
            for (String key : keywords) {
                if (genre.contains(key)) score += 3;
            }
        }

        // popularity bias by mood
        int pop = t.getPopularity();
        switch (normalizeMood(mood)) {
            case "party", "energetic", "happy" -> score += pop / 20.0;
            case "chill", "focus", "sad" -> score += (100 - pop) / 25.0;
            default -> score += 1;
        }

        return score;
    }

    private String normalizeMood(String raw) {
        String m = raw.toLowerCase();
        if (m.contains("happy") || m.contains("joy")) return "happy";
        if (m.contains("chill") || m.contains("calm") || m.contains("relax")) return "chill";
        if (m.contains("sad") || m.contains("down") || m.contains("blue")) return "sad";
        if (m.contains("energy") || m.contains("hype") || m.contains("workout")) return "energetic";
        if (m.contains("focus") || m.contains("study") || m.contains("work")) return "focus";
        if (m.contains("party") || m.contains("club")) return "party";
        if (m.contains("love") || m.contains("romance")) return "romantic";
        if (m.contains("angry") || m.contains("mad") || m.contains("rage")) return "angry";
        return "chill";
    }

    private List<Track> softShuffleByScore(List<Track> ranked) {
        // keep general order, shuffle small windows
        List<Track> result = new ArrayList<>(ranked);
        Random r = new Random();
        for (int i = 0; i < result.size(); i += 4) {
            int end = Math.min(i + 4, result.size());
            Collections.shuffle(result.subList(i, end), r);
        }
        return result;
    }
}
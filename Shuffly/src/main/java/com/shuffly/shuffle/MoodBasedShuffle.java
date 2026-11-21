
package com.shuffly.shuffle;

import com.shuffly.model.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MoodBasedShuffle implements ShuffleStrategy {

    private final String mood;

    public MoodBasedShuffle(String mood) {
        this.mood = normalizeMood(mood);
    }

    @Override
    public List<Track> shuffle(List<Track> songs, List<Track> recentlyPlayed) {

        List<Track> result = new ArrayList<>(songs);

        // Put the best mood matches first
        result.sort(Comparator.comparingInt(this::score).reversed());

        // Randomize songs that have the same score
        int start = 0;

        while (start < result.size()) {
            int currentScore = score(result.get(start));
            int end = start + 1;

            while (end < result.size()
                    && score(result.get(end)) == currentScore) {
                end++;
            }

            Collections.shuffle(result.subList(start, end));
            start = end;
        }

        return result;
    }

    private int score(Track track) {

        if (track == null) {
            return 0;
        }

        for (String tag : track.getMoodTags()) {
            if (normalizeMood(tag).equals(mood)) {
                return 10;
            }
        }

        return 0;
    }

    private String normalizeMood(String raw) {

        if (raw == null) {
            return "chill";
        }

        String m = raw.toLowerCase(Locale.ROOT);

        if (m.contains("happy") || m.contains("joy")) return "happy";
        if (m.contains("chill") || m.contains("calm")
                || m.contains("relax")) return "chill";
        if (m.contains("sad") || m.contains("down")
                || m.contains("blue")) return "sad";
        if (m.contains("energy") || m.contains("hype")
                || m.contains("workout")) return "energetic";
        if (m.contains("focus") || m.contains("study")
                || m.contains("work")) return "focus";
        if (m.contains("party") || m.contains("club")) return "party";
        if (m.contains("love") || m.contains("romance")) return "romantic";
        if (m.contains("angry") || m.contains("mad")
                || m.contains("rage")) return "angry";

        return m;
    }
}

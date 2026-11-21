
package com.shuffly.shuffle;

import com.shuffly.model.Track;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class MoodTagger {

    public static void assignTags(Track track) {

        if (track == null || track.areMoodTagsCustomized()) {
            return;
        }

        String title = normalize(track.getName());

        Set<String> tags = new LinkedHashSet<>();

        // Happy
        if (containsAny(title,
                "happy", "smile", "sunshine", "good day",
                "good time", "beautiful", "joy")) {
            tags.add("happy");
        }

        // Chill
        if (containsAny(title,
                "calm", "sleep", "dream", "slow",
                "relax", "rain", "peace", "quiet")) {
            tags.add("chill");
        }

        // Sad
        if (containsAny(title,
                "sad", "alone", "goodbye", "tears",
                "miss you", "broken", "hurt",
                "without you", "suffocating", "gone")) {
            tags.add("sad");
        }

        // Energetic
        if (containsAny(title,
                "run", "fire", "fight", "alive",
                "power", "strong", "energy", "rise")) {
            tags.add("energetic");
        }

        // Focus
        if (containsAny(title,
                "study", "focus", "instrumental",
                "piano", "concentration")) {
            tags.add("focus");
        }

        // Party
        if (containsAny(title,
                "party", "dance", "tonight",
                "club", "celebrate")) {
            tags.add("party");
        }

        // Romantic
        if (containsAny(title,
                "love", "baby", "kiss", "forever",
                "romance", "lover", "darling")) {
            tags.add("romantic");
        }

        // Angry
        if (containsAny(title,
                "rage", "angry", "hate", "war",
                "destroy", "betrayed", "revenge")) {
            tags.add("angry");
        }

        track.setMoodTags(new ArrayList<>(tags));
    }

    private static boolean containsAny(String text, String... keywords) {

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private static String normalize(String text) {

        if (text == null) {
            return "";
        }

        return text.toLowerCase(Locale.ROOT);
    }
}

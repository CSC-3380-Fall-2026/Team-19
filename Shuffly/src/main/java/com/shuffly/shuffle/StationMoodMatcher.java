
package com.shuffly.shuffle;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class StationMoodMatcher {

    public static List<String> getMoodsFromDescription(String description) {

        if (description == null || description.isBlank()) {
            return new ArrayList<>();
        }

        String text = description.toLowerCase(Locale.ROOT);

        Set<String> moods = new LinkedHashSet<>();

        // Happy
        if (containsAny(text, "happy", "fun", "joy", "cheerful",
                "positive", "upbeat", "feel good", "sunshine")) {
            moods.add("happy");
        }

        // Chill
        if (containsAny(text, "chill", "relax", "calm", "peaceful",
                "laid back", "sleep", "quiet", "rainy", "mellow")) {
            moods.add("chill");
        }

        // Sad
        if (containsAny(text, "sad", "heartbreak", "emotional",
                "lonely", "melancholy", "upset", "crying")) {
            moods.add("sad");
        }

        // Energetic
        if (containsAny(text, "energetic", "energy", "workout",
                "gym", "running", "intense", "powerful", "hype")) {
            moods.add("energetic");
        }

        // Focus
        if (containsAny(text, "focus", "study", "studying",
                "concentrate", "concentration", "productive",
                "homework", "working")) {
            moods.add("focus");
        }

        // Party
        if (containsAny(text, "party", "dancing", "dance",
                "club", "celebration", "celebrate", "night out")) {
            moods.add("party");
        }

        // Romantic
        if (containsAny(text, "romantic", "romance", "love",
                "date night", "intimate", "relationship")) {
            moods.add("romantic");
        }

        // Angry
        if (containsAny(text, "angry", "anger", "rage",
                "aggressive", "frustrated", "revenge")) {
            moods.add("angry");
        }

        return new ArrayList<>(moods);
    }

    private static boolean containsAny(String text, String... keywords) {

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}

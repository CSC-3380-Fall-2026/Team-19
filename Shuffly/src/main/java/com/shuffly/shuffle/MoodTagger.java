
package com.shuffly.shuffle;

import com.shuffly.model.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class MoodTagger {
	
	private static final Map<String, Map<String, Integer>> MOOD_KEYWORDS =
	        new HashMap<>();
	static {
	    addMood("happy", 3,
	            "happy", "smile", "sunshine", "joy", "beautiful",
	            "wonderful", "bright", "paradise", "celebrate",
	            "summer", "bliss", "freedom", "fun");

	    addMood("chill", 3,
	            "relax", "calm", "peace", "dream", "sleep",
	            "quiet", "rain", "moonlight", "breeze",
	            "floating", "ambient", "mellow", "slow");

	    addMood("sad", 3,
	            "heartbreak", "tears", "lonely", "goodbye",
	            "broken", "miss you", "without you", "grief",
	            "empty", "regret", "lost", "farewell");

	    addMood("energetic", 3,
	            "fire", "power", "fight", "run", "alive",
	            "strong", "energy", "rise", "wild",
	            "rush", "adrenaline", "intense", "unstoppable");

	    addMood("focus", 3,
	            "instrumental", "concentration", "study",
	            "piano", "ambient", "meditation", "deep work",
	            "minimal", "classical", "background");

	    addMood("party", 3,
	            "party", "dance", "club", "tonight",
	            "celebration", "disco", "remix", "celebrate",
	            "turn up", "dancefloor");

	    addMood("romantic", 3,
	            "love", "baby", "kiss", "forever",
	            "romance", "lover", "darling", "heart",
	            "sweetheart", "together", "crush");

	    addMood("angry", 3,
	            "rage", "angry", "hate", "war",
	            "destroy", "revenge", "betrayed", "fury",
	            "violence", "fight", "furious");
	}
	
	private static void addMood(String mood, int weight, String... keywords) {
	    Map<String, Integer> keywordsWithWeights = new HashMap<>();

	    for (String keyword : keywords) {
	        keywordsWithWeights.put(keyword, weight);
	    }

	    MOOD_KEYWORDS.put(mood, keywordsWithWeights);
	}

	public static void assignTags(Track track) {

	    if (track == null || track.areMoodTagsCustomized()) {
	        return;
	    }

	    String title = normalize(track.getName());

	    Map<String, Integer> scores = new HashMap<>();

	    // Calculate a score for every mood.
	    for (String mood : MOOD_KEYWORDS.keySet()) {

	        int score = 0;

	        Map<String, Integer> keywords = MOOD_KEYWORDS.get(mood);

	        for (Map.Entry<String, Integer> entry : keywords.entrySet()) {

	            if (containsPhrase(title, entry.getKey())) {
	                score += entry.getValue();
	            }
	        }

	        scores.put(mood, score);
	    }

	    // Assign moods that reach the minimum score.
	    List<String> tags = new ArrayList<>();

	    for (Map.Entry<String, Integer> entry : scores.entrySet()) {

	        if (entry.getValue() >= 3) {
	            tags.add(entry.getKey());
	        }
	    }

	    track.setMoodTags(tags);
	}
	
	private static boolean containsPhrase(String text, String phrase) {

	    return (" " + text + " ").contains(" " + phrase + " ");
	}

    private static String normalize(String text) {

        if (text == null) {
            return "";
        }

        return text.toLowerCase(Locale.ROOT);
    }
}

package com.shuffly;

import com.shuffly.model.Track;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtistSpacingShuffle implements ShuffleStrategy {
    @Override
    public List<Track> shuffle(List<Track> songs, List<Track> recentlyPlayed) {
        List<Track> pool = new ArrayList<>(songs);
        Collections.shuffle(pool);
        List<Track> result = new ArrayList<>();

        String lastArtist = recentlyPlayed.isEmpty() ? null
                : recentlyPlayed.get(recentlyPlayed.size() - 1).getArtist();

        while (!pool.isEmpty()) {
            int pick = 0; // fallback if only one artist is left
            for (int i = 0; i < pool.size(); i++) {
                if (!pool.get(i).getArtist().equalsIgnoreCase(lastArtist)) {
                    pick = i;
                    break;
                }
            }
            Track chosen = pool.remove(pick);
            result.add(chosen);
            lastArtist = chosen.getArtist();
        }
        return result;
    }
}

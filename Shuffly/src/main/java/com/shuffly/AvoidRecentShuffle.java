package com.shuffly;

import com.shuffly.model.Track;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AvoidRecentShuffle implements ShuffleStrategy {
    @Override
    public List<Track> shuffle(List<Track> songs, List<Track> recentlyPlayed) {
        Set<String> recentIds = new HashSet<>();
        for (Track t : recentlyPlayed) recentIds.add(t.getId());

        List<Track> fresh = new ArrayList<>();
        List<Track> stale = new ArrayList<>();
        for (Track t : songs) {
            (recentIds.contains(t.getId()) ? stale : fresh).add(t);
        }
        Collections.shuffle(fresh);
        Collections.shuffle(stale);
        fresh.addAll(stale);
        return fresh;
    }
}

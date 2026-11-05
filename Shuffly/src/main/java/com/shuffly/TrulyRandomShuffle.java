package com.shuffly;

import com.shuffly.model.Track;
import java.util.List;

public class TrulyRandomShuffle implements ShuffleStrategy {
    @Override
    public List<Track> shuffle(List<Track> songs, List<Track> recentlyPlayed) {
        return ShuffleAlgorithm.shuffle(songs);
    }
}

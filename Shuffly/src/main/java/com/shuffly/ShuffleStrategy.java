package com.shuffly;

import com.shuffly.model.Track;
import java.util.List;

public interface ShuffleStrategy {
    List<Track> shuffle(List<Track> songs, List<Track> recentlyPlayed);
}

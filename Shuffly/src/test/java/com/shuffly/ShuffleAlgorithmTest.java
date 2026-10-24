
package com.shuffly;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class ShuffleAlgorithmTest {

    @Test
    void shuffledListContainsAllOriginalSongs() {
        List<String> songs = List.of("Song A", "Song B", "Song C");

        List<String> shuffled = ShuffleAlgorithm.shuffle(songs);

        assertEquals(songs.size(), shuffled.size());
        assertTrue(shuffled.containsAll(songs));
    }

    @Test
    void originalListRemainsUnchanged() {
        List<String> songs = new ArrayList<>(
            List.of("Song A", "Song B", "Song C")
        );
        List<String> original = new ArrayList<>(songs);

        ShuffleAlgorithm.shuffle(songs);

        assertEquals(original, songs);
    }

    @Test
    void emptyListReturnsEmptyList() {
        List<String> songs = List.of();

        List<String> shuffled = ShuffleAlgorithm.shuffle(songs);

        assertTrue(shuffled.isEmpty());
    }

    @Test
    void singleSongListStillContainsSameSong() {
        List<String> songs = List.of("Song A");

        List<String> shuffled = ShuffleAlgorithm.shuffle(songs);

        assertEquals(List.of("Song A"), shuffled);
    }
}

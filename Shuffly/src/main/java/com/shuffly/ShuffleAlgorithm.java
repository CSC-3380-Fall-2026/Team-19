package com.shuffly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShuffleAlgorithm {

    public static <T> List<T> shuffle(List<T> songs) {
        List<T> shuffledSongs = new ArrayList<>(songs);
        Collections.shuffle(shuffledSongs);
        return shuffledSongs;
    }
}
package com.shuffly;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class ShuffleAlgorithmTest {

    @Test
    void shuffleListPreservesElementsAndReturnsCopy() {
        List<String> input = new ArrayList<>(List.of("A", "B", "C", "D", "E"));

        List<String> shuffled = ShuffleAlgorithm.shuffle(input, 42L);

        assertNotSame(input, shuffled);
        assertEquals(input.size(), shuffled.size());
        assertEquals(new HashSet<>(input), new HashSet<>(shuffled));
    }

    @Test
    void shuffleWithSameSeedProducesSameOrder() {
        List<Integer> input = IntStream.rangeClosed(1, 10).boxed().toList();

        List<Integer> first = ShuffleAlgorithm.shuffle(input, 123L);
        List<Integer> second = ShuffleAlgorithm.shuffle(input, 123L);

        assertEquals(first, second);
    }

    @Test
    void shuffleHandlesEmptyAndSingleItemCollections() {
        List<String> empty = ShuffleAlgorithm.shuffle(List.of(), 7L);
        List<String> singleton = ShuffleAlgorithm.shuffle(List.of("Only"), 7L);

        assertEquals(List.of(), empty);
        assertEquals(List.of("Only"), singleton);
    }

    @Test
    void shuffleArrayPreservesValues() {
        int[] input = {1, 2, 3, 4, 5, 6};

        int[] shuffled = ShuffleAlgorithm.shuffle(input, 9L);

        assertEquals(input.length, shuffled.length);
        Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        Set<Integer> actual = Arrays.stream(shuffled).boxed().collect(java.util.stream.Collectors.toSet());
        assertEquals(expected, actual);
        assertArrayEquals(new int[] {1, 2, 3, 4, 5, 6}, input);
    }
}

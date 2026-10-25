package com.shuffly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class ShuffleAlgorithm {

    private ShuffleAlgorithm() {
    }

    public static <T> List<T> shuffle(List<T> items) {
        return shuffle(items, System.nanoTime());
    }

    public static <T> List<T> shuffle(List<T> items, long seed) {
        if (items == null) {
            throw new IllegalArgumentException("items cannot be null");
        }

        List<T> copy = new ArrayList<>(items);
        Random random = new Random(seed);

        for (int i = copy.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            T temp = copy.get(i);
            copy.set(i, copy.get(j));
            copy.set(j, temp);
        }

        return copy;
    }

    public static int[] shuffle(int[] values) {
        return shuffle(values, System.nanoTime());
    }

    public static int[] shuffle(int[] values, long seed) {
        if (values == null) {
            throw new IllegalArgumentException("values cannot be null");
        }

        int[] copy = Arrays.copyOf(values, values.length);
        Random random = new Random(seed);

        for (int i = copy.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = copy[i];
            copy[i] = copy[j];
            copy[j] = temp;
        }

        return copy;
    }
}

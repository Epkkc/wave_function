package com.example.demo.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    private static final Random random = new Random();

    public static <T> List<T> randomUniqueValues(Collection<T> list, int amount) {
        int size = list.size();

        if (amount < 1 || size < amount) return List.of();

        List<T> copy = new ArrayList<>(list);

        if (amount == size - 1) {
            copy.remove(random.nextInt(size));
            return copy;
        }

        List<T> result = new ArrayList<>(list);

        for (int i = 0; i < amount; i++) {
            result.add(copy.remove(random.nextInt(copy.size())));
        }

        return result;
    }

    public static <T> T randomValue(Collection<T> list) {
        int size = list.size();

        List<T> copy = new ArrayList<>(list);

        if (size < 1) return null;

        return copy.get(random.nextInt(size));
    }

}

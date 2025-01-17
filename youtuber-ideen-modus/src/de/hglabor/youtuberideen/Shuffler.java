package de.hglabor.youtuberideen;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

public final class Shuffler<T> implements Comparator<T> {
    private final Map<T, Integer> map = new IdentityHashMap<>();
    private final Random random;

    public Shuffler() {
        this(new Random());
    }

    public Shuffler(Random random) {
        this.random = random;
    }

    @Override
    public int compare(T t1, T t2) {
        return Integer.compare(valueFor(t1), valueFor(t2));
    }

    private int valueFor(T t) {
        synchronized (map) {
            return map.computeIfAbsent(t, ignore -> random.nextInt());
        }
    }
}
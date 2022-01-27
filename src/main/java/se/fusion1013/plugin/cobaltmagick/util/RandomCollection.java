package se.fusion1013.plugin.cobaltmagick.util;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public E next() {
        if (map.isEmpty()) return null;
        double value = random.nextDouble() * total;
        Map.Entry<Double, E> entry = map.higherEntry(value);
        if (entry == null) return null;
        return entry.getValue();
    }
}

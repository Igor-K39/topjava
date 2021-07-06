package ru.javawebinar.topjava.util;

public class Util {
    public static <T extends Comparable<T>> boolean isBetweenInclusive(T value, T from, T to) {
        return value.compareTo(from) >= 0 && value.compareTo(to) <= 0;
    }

    public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T value, T from, T to) {
        return value.compareTo(from) >= 0 && value.compareTo(to) < 0;
    }
}

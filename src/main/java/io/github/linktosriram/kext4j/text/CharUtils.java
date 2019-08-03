package io.github.linktosriram.kext4j.text;

import org.jetbrains.annotations.Contract;

public final class CharUtils {

    @Contract(value = " -> fail", pure = true)
    private CharUtils() {
        throw new AssertionError();
    }

    public static boolean equals(final char first, final char second) {
        return equals(first, second, false);
    }

    public static boolean equals(final char first, final char second, final boolean ignoreCase) {
        if (first == second) {
            return true;
        }
        if (!ignoreCase) {
            return false;
        }
        if (Character.toUpperCase(first) == Character.toUpperCase(second)) {
            return true;
        }
        return Character.toLowerCase(first) == Character.toLowerCase(second);
    }
}

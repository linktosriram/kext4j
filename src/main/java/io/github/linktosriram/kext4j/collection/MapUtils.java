package io.github.linktosriram.kext4j.collection;

import org.jetbrains.annotations.Contract;

public final class MapUtils {

    private static final int INT_MAX_POWER_OF_TWO = Integer.MAX_VALUE / 2 + 1;

    @Contract(value = " -> fail", pure = true)
    private MapUtils() {
        throw new AssertionError();
    }

    /**
     * Calculate the initial capacity of a map, based on Guava's com.google.common.collect.Maps approach. This is equivalent
     * to the Collection constructor for HashSet, (c.size()/.75f) + 1, but provides further optimisations for very small or
     * very large sizes, allows support non-collection classes, and provides consistency for all map based class construction.
     */
    @Contract(pure = true)
    public static int mapCapacity(final int expectedSize) {
        if (expectedSize < 3) {
            return expectedSize + 1;
        }
        if (expectedSize < INT_MAX_POWER_OF_TWO) {
            return expectedSize + expectedSize / 3;
        }
        return Integer.MAX_VALUE;   // any large value
    }
}

package io.github.linktosriram.kext4j.range;

import org.jetbrains.annotations.NotNull;

public interface ClosedRange<T extends Comparable<T>> {

    @NotNull T getStart();

    @NotNull T getEndInclusive();

    default boolean contains(final @NotNull T value) {
        return value.compareTo(getStart()) >= 0 && value.compareTo(getEndInclusive()) <= 0;
    }

    default boolean isEmpty() {
        return getStart().compareTo(getEndInclusive()) > 0;
    }
}

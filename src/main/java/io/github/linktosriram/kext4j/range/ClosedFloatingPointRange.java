package io.github.linktosriram.kext4j.range;

import org.jetbrains.annotations.NotNull;

public interface ClosedFloatingPointRange<T extends Comparable<T>> extends ClosedRange<T> {

    boolean lessThanOrEquals(T a, T b);

    @Override
    default boolean contains(final @NotNull T value) {
        return lessThanOrEquals(getStart(), value) && lessThanOrEquals(value, getEndInclusive());
    }

    @Override
    default boolean isEmpty() {
        return !lessThanOrEquals(getStart(), getEndInclusive());
    }
}

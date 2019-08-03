package io.github.linktosriram.kext4j.range;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ComparableRange<T extends Comparable<T>> implements ClosedRange<T> {

    private final T start;
    private final T endInclusive;

    @Contract(pure = true)
    public ComparableRange(final T start, final T endInclusive) {
        this.start = start;
        this.endInclusive = endInclusive;
    }

    @Override
    public @NotNull T getStart() {
        return start;
    }

    @Override
    public @NotNull T getEndInclusive() {
        return endInclusive;
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(final Object obj) {
        if (obj instanceof ComparableRange<?>) {
            final ComparableRange<?> other = (ComparableRange<?>) obj;
            return isEmpty() && other.isEmpty() || start.equals(other.start) && endInclusive.equals(other.endInclusive);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return isEmpty() ? -1 : 31 * start.hashCode() + endInclusive.hashCode();
    }

    @NonNls
    @Override
    public String toString() {
        return start + ".." + endInclusive;
    }
}

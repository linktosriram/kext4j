package io.github.linktosriram.kext4j.range;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class IntRange extends IntProgression implements ClosedRange<Integer> {

    public static final IntRange EMPTY = new IntRange(1, 0);

    public IntRange(final int start, final int endInclusive) {
        super(start, endInclusive, 1);
    }

    @Override
    public @NotNull Integer getStart() {
        return getFirst();
    }

    @Override
    public @NotNull Integer getEndInclusive() {
        return getLast();
    }

    @Override
    public boolean contains(final @NotNull Integer value) {
        return getFirst() <= value && value <= getLast();
    }

    @Override
    public boolean isEmpty() {
        return getFirst() > getLast();
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(final Object obj) {
        if (obj instanceof IntRange) {
            final IntRange other = (IntRange) obj;
            return isEmpty() && other.isEmpty() || getFirst() == other.getFirst() && getLast() == other.getLast();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return isEmpty() ? -1 : 31 * getFirst() + getLast();
    }

    @NonNls
    @Override
    public String toString() {
        return getFirst() + ".." + getLast();
    }
}

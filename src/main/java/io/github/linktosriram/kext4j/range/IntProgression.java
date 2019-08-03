package io.github.linktosriram.kext4j.range;

import io.github.linktosriram.kext4j.collection.IntIterator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

class IntProgression implements Iterable<Integer> {

    private final int first;
    private final int last;
    private final int step;

    IntProgression(final int start, final int endInclusive, final int step) {
        if (step == 0) {
            throw new IllegalArgumentException("Step must be non-zero.");
        }
        if (step == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Step must be greater than Integer.MIN_VALUE to avoid overflow on negation.");
        }
        first = start;
        last = endInclusive;
        this.step = step;
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }

    public int getStep() {
        return step;
    }

    @Override
    public @NotNull IntIterator iterator() {
        return new IntProgressionIterator(first, last, step);
    }

    public boolean isEmpty() {
        return step > 0 ? first > last : first < last;
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(final Object obj) {
        if (obj instanceof IntProgression) {
            final IntProgression other = (IntProgression) obj;
            return isEmpty() && other.isEmpty() || first == other.first && last == other.last && step == other.step;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return isEmpty() ? -1 : 31 * (31 * first + last) + step;
    }

    @NonNls
    @Override
    public String toString() {
        return step > 0 ? first + ".." + last + " step " + step : first + " downTo " + last + " step " + (-step);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull IntProgression fromClosedRange(final int rangeStart, final int rangeEnd, final int step) {
        return new IntProgression(rangeStart, rangeEnd, step);
    }
}

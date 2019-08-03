package io.github.linktosriram.kext4j.range;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ClosedDoubleRange implements ClosedFloatingPointRange<Double> {

    private final double start;
    private final double endInclusive;

    @Contract(pure = true)
    public ClosedDoubleRange(final double start, final double endInclusive) {
        this.start = start;
        this.endInclusive = endInclusive;
    }

    @Override
    public @NotNull Double getStart() {
        return start;
    }

    @Override
    public @NotNull Double getEndInclusive() {
        return endInclusive;
    }

    @Override
    public boolean lessThanOrEquals(final Double a, final Double b) {
        return Double.compare(a, b) <= 0;
    }

    @Override
    public boolean contains(final @NotNull Double value) {
        return value.compareTo(start) >= 0 && value.compareTo(endInclusive) <= 0;
    }

    @Override
    public boolean isEmpty() {
        return !(start <= endInclusive);
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(final Object obj) {
        if (obj instanceof ClosedDoubleRange) {
            final ClosedDoubleRange other = (ClosedDoubleRange) obj;
            return isEmpty() && other.isEmpty() || Double.compare(start, other.start) == 0 && Double.compare(endInclusive, other.endInclusive) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return isEmpty() ? -1 : (int) (31.0 * start + endInclusive);
    }

    @NonNls
    @Override
    public String toString() {
        return start + ".." + endInclusive;
    }
}

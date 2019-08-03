package io.github.linktosriram.kext4j.range;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RangeUtils {

    @Contract(value = " -> fail", pure = true)
    private RangeUtils() {
        throw new AssertionError();
    }

    // Ensures that this value is not less than the specified minimumValue.
    @Contract(pure = true)
    public static @NotNull <T extends Comparable<T>> T coerceAtLeast(final @NotNull T value, final @NotNull T minimumValue) {
        return value.compareTo(minimumValue) < 0 ? minimumValue : value;
    }

    @Contract(pure = true)
    public static byte coerceAtLeast(final byte value, final byte minimumValue) {
        return value < minimumValue ? minimumValue : value;
    }

    @Contract(pure = true)
    public static short coerceAtLeast(final short value, final short minimumValue) {
        return value < minimumValue ? minimumValue : value;
    }

    @Contract(pure = true)
    public static int coerceAtLeast(final int value, final int minimumValue) {
        return Math.max(value, minimumValue);
    }

    @Contract(pure = true)
    public static long coerceAtLeast(final long value, final long minimumValue) {
        return Math.max(value, minimumValue);
    }

    @Contract(pure = true)
    public static float coerceAtLeast(final float value, final float minimumValue) {
        return Float.compare(value, minimumValue) < 0 ? minimumValue : value;
    }

    @Contract(pure = true)
    public static double coerceAtLeast(final double value, final double minimumValue) {
        return Double.compare(value, minimumValue) < 0 ? minimumValue : value;
    }

    // Ensures that this value is not greater than the specified maximumValue.
    @Contract(pure = true)
    public static @NotNull <T extends Comparable<T>> T coerceAtMost(final @NotNull T value, final @NotNull T maximumValue) {
        return value.compareTo(maximumValue) > 0 ? maximumValue : value;
    }

    @Contract(pure = true)
    public static byte coerceAtMost(final byte value, final byte maximumValue) {
        return value > maximumValue ? maximumValue : value;
    }

    @Contract(pure = true)
    public static short coerceAtMost(final short value, final short maximumValue) {
        return value > maximumValue ? maximumValue : value;
    }

    @Contract(pure = true)
    public static int coerceAtMost(final int value, final int maximumValue) {
        return Math.min(value, maximumValue);
    }

    @Contract(pure = true)
    public static long coerceAtMost(final long value, final long maximumValue) {
        return Math.min(value, maximumValue);
    }

    @Contract(pure = true)
    public static float coerceAtMost(final float value, final float maximumValue) {
        return Float.compare(value, maximumValue) > 0 ? maximumValue : value;
    }

    @Contract(pure = true)
    public static double coerceAtMost(final double value, final double maximumValue) {
        return Double.compare(value, maximumValue) > 0 ? maximumValue : value;
    }

    // Ensures that this value lies in the specified range minimumValue..maximumValue.
    public static @NotNull <T extends Comparable<T>> T coerceIn(final @NotNull T value, @NonNls final @Nullable T minimumValue,
                                                                @NonNls final @Nullable T maximumValue) {
        if (minimumValue != null && maximumValue != null) {
            if (minimumValue.compareTo(maximumValue) > 0) {
                throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue +
                    " is less than minimum " + minimumValue + ".");
            }

            if (value.compareTo(minimumValue) < 0) {
                return minimumValue;
            }
            if (value.compareTo(maximumValue) > 0) {
                return maximumValue;
            }
        } else {
            if (minimumValue != null && value.compareTo(minimumValue) < 0) {
                return minimumValue;
            }
            if (maximumValue != null && value.compareTo(maximumValue) > 0) {
                return maximumValue;
            }
        }
        return value;
    }

    @Contract(pure = true)
    public static byte coerceIn(final byte value, @NonNls final byte minimumValue, @NonNls final byte maximumValue) {
        if (minimumValue > maximumValue) {
            throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue +
                " is less than minimum " + minimumValue + ".");
        }

        if (value < minimumValue) {
            return minimumValue;
        }
        if (value > maximumValue) {
            return maximumValue;
        }
        return value;
    }

    @Contract(pure = true)
    public static short coerceIn(final short value, @NonNls final short minimumValue, @NonNls final short maximumValue) {
        if (minimumValue > maximumValue) {
            throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue +
                " is less than minimum " + minimumValue + ".");
        }

        if (value < minimumValue) {
            return minimumValue;
        }
        if (value > maximumValue) {
            return maximumValue;
        }
        return value;
    }

    @Contract(pure = true)
    public static int coerceIn(final int value, @NonNls final int minimumValue, @NonNls final int maximumValue) {
        if (minimumValue > maximumValue) {
            throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue +
                " is less than minimum " + minimumValue + ".");
        }

        if (value < minimumValue) {
            return minimumValue;
        }
        if (value > maximumValue) {
            return maximumValue;
        }
        return value;
    }

    @Contract(pure = true)
    public static long coerceIn(final long value, @NonNls final long minimumValue, @NonNls final long maximumValue) {
        if (minimumValue > maximumValue) {
            throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue +
                " is less than minimum " + minimumValue + ".");
        }

        if (value < minimumValue) {
            return minimumValue;
        }
        if (value > maximumValue) {
            return maximumValue;
        }
        return value;
    }

    public static float coerceIn(final float value, @NonNls final float minimumValue, @NonNls final float maximumValue) {
        if (Float.compare(minimumValue, maximumValue) > 0) {
            throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue +
                " is less than minimum " + minimumValue + ".");
        }

        if (Float.compare(value, minimumValue) < 0) {
            return minimumValue;
        }
        if (Float.compare(value, maximumValue) > 0) {
            return maximumValue;
        }
        return value;
    }

    public static double coerceIn(final double value, @NonNls final double minimumValue, @NonNls final double maximumValue) {
        if (Double.compare(minimumValue, maximumValue) > 0) {
            throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue +
                " is less than minimum " + minimumValue + ".");
        }

        if (Double.compare(value, minimumValue) < 0) {
            return minimumValue;
        }
        if (Double.compare(value, maximumValue) > 0) {
            return maximumValue;
        }
        return value;
    }

    // Creates a range from start Comparable value to the specified end value.
    // Start value needs to be smaller than end value, otherwise the returned range will be empty.
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull <T extends Comparable<T>> ClosedRange<T> rangeTo(final T start, final T endInclusive) {
        return new ComparableRange<>(start, endInclusive);
    }

    // Creates a range from this Double value to the specified that value.
    // Numbers are compared with the ends of this range according to IEEE-754.
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull ClosedFloatingPointRange<Double> rangeTo(final Double start, final Double endInclusive) {
        return new ClosedDoubleRange(start, endInclusive);
    }
}

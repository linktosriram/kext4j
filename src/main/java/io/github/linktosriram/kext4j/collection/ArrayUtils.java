package io.github.linktosriram.kext4j.collection;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.function.Predicate;

public final class ArrayUtils {

    @Contract(value = " -> fail", pure = true)
    private ArrayUtils() {
        throw new AssertionError();
    }

    // Returns the single element, or throws an exception if the array is empty or has more than one element.
    public static char single(final @NotNull char[] chars) {
        switch (chars.length) {
            case 0:
                throw new NoSuchElementException("Array is empty.");
            case 1:
                return chars[0];
            default:
                throw new IllegalArgumentException("Array has more than one element.");
        }
    }

    // Returns true if at least one element matches the given predicate.
    public static boolean any(final @NotNull char[] chars, final @NotNull Predicate<? super Character> predicate) {
        for (final char ch : chars) {
            if (predicate.test(ch)) {
                return true;
            }
        }
        return false;
    }
}

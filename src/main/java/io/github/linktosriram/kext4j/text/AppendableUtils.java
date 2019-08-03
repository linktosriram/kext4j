package io.github.linktosriram.kext4j.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

public final class AppendableUtils {

    @Contract(value = " -> fail", pure = true)
    private AppendableUtils() {
        throw new AssertionError();
    }

    public static <T> void appendElement(final @NotNull Appendable appendable, final @NotNull T element,
                                         final @Nullable Function<T, ? extends CharSequence> transform) {
        if (transform != null) {
            try {
                appendable.append(transform.apply(element));
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        } else if (element instanceof CharSequence) {
            try {
                appendable.append((CharSequence) element);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        } else if (element instanceof Character) {
            try {
                appendable.append((Character) element);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            try {
                appendable.append(element.toString());
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}

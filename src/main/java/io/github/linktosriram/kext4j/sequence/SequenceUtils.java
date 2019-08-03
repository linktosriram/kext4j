package io.github.linktosriram.kext4j.sequence;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static io.github.linktosriram.kext4j.collection.CollectionUtils.optimizeReadOnlyList;
import static io.github.linktosriram.kext4j.text.AppendableUtils.appendElement;

public final class SequenceUtils {

    @Contract(value = " -> fail", pure = true)
    private SequenceUtils() {
        throw new AssertionError();
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull <T, R> Sequence<R> map(final Sequence<T> seq, final Function<? super T, ? extends R> transform) {
        return new TransformingSequence<>(seq, transform);
    }

    public static @NotNull <T> List<T> toList(final @NotNull Sequence<T> seq) {
        return optimizeReadOnlyList(toMutableList(seq));
    }

    public static @NotNull <T> List<T> toMutableList(final @NotNull Sequence<T> seq) {
        return toCollection(seq, new ArrayList<>());
    }

    @Contract("_, _ -> param2")
    public static @NotNull <T, C extends Collection<T>> C toCollection(final @NotNull Sequence<T> seq, final @NotNull C destination) {
        final Iterator<T> iterator = seq.iterator();
        while (iterator.hasNext()) {
            destination.add(iterator.next());
        }
        return destination;
    }

    // TODO: implement overloads
    public static @NotNull <T> String joinToString(final @NotNull Sequence<T> seq, final @NotNull CharSequence separator) {
        return joinToString(seq, separator, "", "", -1, "...", null);
    }

    public static @NotNull <T> String joinToString(final @NotNull Sequence<T> seq, final @NotNull CharSequence separator,
                                                   final @NotNull CharSequence prefix, final @NotNull CharSequence postfix, final int limit,
                                                   final @NotNull CharSequence truncated,
                                                   final @Nullable Function<? super T, ? extends CharSequence> transform) {
        return joinTo(seq, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform).toString();
    }

    @Contract("_, _, _, _, _, _, _, _ -> param2")
    public static <T, A extends Appendable> A joinTo(final @NotNull Sequence<T> seq, final @NotNull A buffer, final @NotNull CharSequence separator,
                                                     final @NotNull CharSequence prefix, final @NotNull CharSequence postfix, final int limit,
                                                     final @NotNull CharSequence truncated,
                                                     final @Nullable Function<? super T, ? extends CharSequence> transform) {
        try {
            buffer.append(prefix);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        int count = 0;
        final Iterator<T> iterator = seq.iterator();
        while (iterator.hasNext()) {
            if (++count > 1) {
                try {
                    buffer.append(separator);
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            if (limit < 0 || count <= limit) {
                final T element = iterator.next();
                appendElement(buffer, element, transform);
            } else {
                break;
            }
        }
        if (limit >= 0 && count > limit) {
            try {
                buffer.append(truncated);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        try {
            buffer.append(postfix);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return buffer;
    }
}

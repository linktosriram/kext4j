package io.github.linktosriram.kext4j.collection;

import io.github.linktosriram.kext4j.text.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.github.linktosriram.kext4j.text.AppendableUtils.appendElement;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class CollectionUtils {

    @Contract(value = " -> fail", pure = true)
    private CollectionUtils() {
        throw new AssertionError();
    }

    // Returns the first element matching the given predicate, or null if element was not found.
    public static @Nullable <T> T firstOrNull(final @NotNull Iterable<? extends T> iterable, final @NotNull Predicate<? super T> predicate) {
        for (final T element : iterable) {
            if (predicate.test(element)) {
                return element;
            }
        }
        return null;
    }

    // Returns the single element, or throws an exception if the collection is empty or has more than one element.
    public static @NotNull <T> T single(final @NotNull Iterable<T> iterable) {
        if (iterable instanceof List) {
            return single((List<T>) iterable);
        } else {
            final Iterator<T> iterator = iterable.iterator();
            if (!iterator.hasNext()) {
                throw new NoSuchElementException("Collection is empty.");
            }
            final T single = iterator.next();
            if (iterator.hasNext()) {
                throw new IllegalArgumentException("Collection has more than one element.");
            }
            return single;
        }
    }

    // Returns the single element, or throws an exception if the list is empty or has more than one element.
    public static @NotNull <T> T single(final @NotNull List<T> list) {
        final int size = list.size();
        switch (list.size()) {
            case 0:
                throw new NoSuchElementException("List is empty.");
            case 1:
                return list.get(0);
            default:
                throw new IllegalArgumentException("List has more than one element.");
        }
    }

    // Adds all elements of the given elements collection to the Collection.
    public static <T> boolean addAll(final @NotNull Collection<? super T> collection, final @NotNull Iterable<T> elements) {
        if (elements instanceof Collection) {
            return collection.addAll((Collection<T>) elements);
        } else {
            boolean result = false;
            for (final T item : elements) {
                if (collection.add(item)) {
                    result = true;
                }
            }
            return result;
        }
    }

    // Returns the value for the given key. If the key is not found in the map, calls the defaultValue function, puts its result into the map under the given key and
    // returns it.
    // Note that the operation is not guaranteed to be atomic if the map is being modified concurrently.
    public static @NotNull <K, V> V getOrPut(final @NotNull Map<K, V> map, final @NotNull K key, final @NotNull Supplier<? extends V> defaultValue) {
        final V value = map.get(key);
        if (value == null) {
            final V result = defaultValue.get();
            map.put(key, result);
            return result;
        } else {
            return value;
        }
    }

    // Returns true if all elements match the given predicate.
    public static <T> boolean all(final @NotNull Iterable<T> iterable, final @NotNull Predicate<? super T> predicate) {
        if (iterable instanceof Collection && ((Collection<T>) iterable).isEmpty()) {
            return true;
        }
        final Predicate<? super T> negated = predicate.negate();
        for (final T element : iterable) {
            if (negated.test(element)) {
                return false;
            }
        }
        return true;
    }

    public static @NotNull <T> List<T> optimizeReadOnlyList(final @NotNull List<? extends T> list) {
        switch (list.size()) {
            case 0:
                return emptyList();
            case 1:
                return singletonList(list.get(0));
            default:
                return unmodifiableList(list);
        }
    }

    // Returns a list containing only elements matching the given predicate.
    public static @NotNull <T> List<T> filter(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        return filterTo(iterable, new ArrayList<>(), predicate);
    }

    // Appends all elements matching the given predicate to the given destination.
    @Contract("_, _, _ -> param2")
    public static @NotNull <T, C extends Collection<T>> C filterTo(final @NotNull Iterable<T> iterable, final @NotNull C destination,
                                                                   final @NotNull Predicate<? super T> predicate) {
        for (final T element : iterable) {
            if (predicate.test(element)) {
                destination.add(element);
            }
        }
        return destination;
    }

    // Returns a list containing the results of applying the given transform function to each element in the original collection.
    public static @NotNull <T, R> List<R> map(final @NotNull Iterable<T> iterable, final @NotNull Function<? super T, R> transform) {
        final int capacity = collectionSizeOrDefault(iterable, 10);
        return mapTo(iterable, new ArrayList<>(capacity), transform);
    }

    @Contract("_, _, _ -> param2")
    public static @NotNull <T, R, C extends Collection<R>> C mapTo(final @NotNull Iterable<T> iterable, final @NotNull C destination,
                                                                   final @NotNull Function<? super T, R> transform) {
        for (final T element : iterable) {
            destination.add(transform.apply(element));
        }
        return destination;
    }

    public static @NotNull <T extends Comparable<T>> Optional<T> min(final @NotNull Iterable<T> iterable) {
        final Iterator<T> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return empty();
        }
        T min = iterator.next();
        while (iterator.hasNext()) {
            final T element = iterator.next();
            if (min.compareTo(element) > 0) {
                min = element;
            }
        }
        return of(min);
    }

    public static @NotNull String reindent(final @NotNull List<String> list, final int resultSizeEstimate,
                                           final @NotNull Function<? super String, String> indentAddFunction,
                                           final @NotNull Function<? super String, @Nullable String> indentCutFunction) {
        final int lastIndex = lastIndex(list);
        final List<String> strings = mapIndexedNotNull(list, (index, value) -> {
            if ((index == 0 || index == lastIndex) && StringUtils.isBlank(value)) {
                return null;
            } else {
                final String result = indentCutFunction.apply(value);
                return result != null ? indentAddFunction.apply(result) : value;
            }
        });
        return joinTo(strings, new StringBuilder(resultSizeEstimate), "\n").toString();
    }

    @Contract("_, _, _ -> param2")
    public static @NotNull <T, A extends Appendable> A joinTo(final @NotNull Iterable<T> iterable, final @NotNull A buffer, final @NotNull CharSequence separator) {
        return joinTo(iterable, buffer, separator, "", "", -1, "...", null);
    }

    @Contract("_, _, _, _, _, _, _, _ -> param2")
    public static @NotNull <T, A extends Appendable> A joinTo(final @NotNull Iterable<T> iterable, final @NotNull A buffer, final @NotNull CharSequence separator,
                                                              final @NotNull CharSequence prefix, final @NotNull CharSequence postfix, final int limit,
                                                              final @NotNull CharSequence truncated,
                                                              final @Nullable Function<? super T, ? extends CharSequence> transform) {
        try {
            buffer.append(prefix);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        int count = 0;
        for (final T element : iterable) {
            if (++count > 1) {
                try {
                    buffer.append(separator);
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            if (limit < 0 || count <= limit) {
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

    public static @NotNull <T, R> List<R> mapIndexedNotNull(final @NotNull Iterable<T> iterable,
                                                            final @NotNull BiFunction<? super Integer, ? super T, @Nullable R> transform) {
        return mapIndexedNotNullTo(iterable, new ArrayList<>(), transform);
    }

    @Contract("_, _, _ -> param2")
    public static @NotNull <T, R, C extends Collection<R>> C mapIndexedNotNullTo(final @NotNull Iterable<T> iterable, final @NotNull C destination,
                                                                                 final @NotNull BiFunction<? super Integer, ? super T, @Nullable R> transform) {
        int index = 0;
        for (final T element : iterable) {
            final R result = transform.apply(index, element);
            if (result != null) {
                destination.add(result);
            }
            index++;
        }
        return destination;
    }

    public static <T> int lastIndex(final List<T> list) {
        return list.size() - 1;
    }

    // Private Stuff

    private static <T> int collectionSizeOrDefault(final Iterable<T> iterable, final int defaultValue) {
        return iterable instanceof Collection ? ((Collection<T>) iterable).size() : defaultValue;
    }
}

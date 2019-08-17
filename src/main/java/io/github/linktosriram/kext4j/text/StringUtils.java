package io.github.linktosriram.kext4j.text;

import io.github.linktosriram.kext4j.Pair;
import io.github.linktosriram.kext4j.collection.ArrayUtils;
import io.github.linktosriram.kext4j.collection.CharIterator;
import io.github.linktosriram.kext4j.collection.CollectionUtils;
import io.github.linktosriram.kext4j.function.TriFunction;
import io.github.linktosriram.kext4j.range.IntRange;
import io.github.linktosriram.kext4j.sequence.DelimitedRangesSequence;
import io.github.linktosriram.kext4j.sequence.Sequence;
import io.github.linktosriram.kext4j.sequence.SequenceUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.github.linktosriram.kext4j.range.RangeUtils.coerceAtLeast;
import static io.github.linktosriram.kext4j.range.RangeUtils.coerceAtMost;
import static java.lang.Character.isHighSurrogate;
import static java.lang.Character.isLowSurrogate;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.isWhitespace;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

// Reference: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html
public final class StringUtils {

    @Contract(value = " -> fail", pure = true)
    private StringUtils() {
        throw new AssertionError();
    }

    /**
     * Returns the range of valid character indices for the {@link CharSequence}.
     *
     * @param seq the {@link CharSequence}
     * @return the range of valid character indices for the given {@link CharSequence}
     */
    @Contract("_ -> new")
    public static @NotNull IntRange indices(final @NotNull CharSequence seq) {
        return new IntRange(0, lastIndex(seq));
    }

    /**
     * Returns the index of the last character in the {@link CharSequence} or {@code - 1} if it is empty.
     *
     * @param seq the {@link CharSequence}
     * @return the index of the last character in the {@link CharSequence} or {@code -1} if it is empty
     */
    public static @Range(from = -1, to = Integer.MAX_VALUE - 1) int lastIndex(final @NotNull CharSequence seq) {
        return seq.length() - 1;
    }

    /**
     * Returns {@code true} if all characters in the {@link CharSequence} satisfy the given {@link Predicate}.
     *
     * @param seq       the {@link CharSequence}
     * @param predicate the {@link Predicate}
     * @return {@code true} if all characters in the {@link CharSequence} satisfy the given predicate, {@code false} otherwise
     */
    public static boolean all(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        final int length = seq.length();
        final Predicate<? super Character> negated = predicate.negate();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (negated.test(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the {@link CharSequence} has at least one character.
     *
     * @param seq the {@link CharSequence}
     * @return {@code true} if the {@link CharSequence} has at least one character, {@code false} otherwise
     */
    public static boolean any(final @NotNull CharSequence seq) {
        return seq.length() > 0;
    }

    /**
     * Returns {@code true} if at least one character in the {@link CharSequence} satisfies the given {@link Predicate}.
     *
     * @param seq       the {@link CharSequence}
     * @param predicate the {@link Predicate}
     * @return {@code true} if at least one character in the {@link CharSequence} satisfies the given predicate, {@code false} otherwise
     */
    public static boolean any(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates an {@link Iterable} instance that wraps the given {@link CharSequence}, returning its characters when
     * being iterated.
     *
     * @param seq the {@link CharSequence}
     * @return an {@link Iterable} backing the {@link CharSequence}
     */
    public static @NotNull Iterable<Character> asIterable(final @NotNull CharSequence seq) {
        return seq instanceof String && isEmpty(seq) ? emptyList() : () -> iterator(seq);
    }

    // Creates a new byte input stream for the string.
    @Contract("_ -> new")
    public static @NotNull ByteArrayInputStream byteInputStream(final @NotNull String str) {
        return new ByteArrayInputStream(toByteArray(str, UTF_8));
    }

    // Creates a new byte input stream for the string.
    @Contract("_, _ -> new")
    public static @NotNull ByteArrayInputStream byteInputStream(final @NotNull String str, final @NotNull Charset charset) {
        return new ByteArrayInputStream(toByteArray(str, charset));
    }

    /**
     * Returns a {@link String} having its first letter uppercased, or the original {@link String}, if it's empty or
     * already starts with an upper case letter.
     *
     * @param str the {@link String}
     * @return the {@link String} having its first letter uppercased
     */
    @NonNls
    public static @NotNull String capitalize(final @NotNull String str) {
        return isNotEmpty(str) && isLowerCase(str.charAt(0)) ? str.substring(0, 1).toUpperCase() + str.substring(1) : str;
    }

    /**
     * Splits the {@link CharSequence} into a {@link List} of {@link String} each not exceeding the given size. The last {@link String} in the
     * resulting {@link List} may have less characters than the given size.
     *
     * @param seq  the {@link CharSequence}
     * @param size the number of elements to take in each {@link String}, must be positive and can be greater than the number of elements in the
     *             {@link CharSequence}
     * @return the {@link List} of {@link String} each not exceeding the given size
     */
    public static @NotNull List<String> chunked(final @NotNull CharSequence seq, final @Range(from = 1, to = Integer.MAX_VALUE) int size) {
        return windowed(seq, size, size, true);
    }

    /**
     * Splits the {@link CharSequence} into several {@link CharSequence}s each not exceeding the given size and applies the given transform
     * {@link Function} to an each.
     * <p>
     * Note that the {@link CharSequence} passed to the transform {@link Function} is ephemeral and is valid only inside that {@link Function}.
     * <p>
     * You should not store it or allow it to escape in some way, unless you made a snapshot of it.
     * The last {@link CharSequence} may have less characters than the given size.
     *
     * @param seq       the {@link CharSequence}
     * @param size      the number of elements to take in each {@link String}, must be positive and can be greater
     *                  than the number of elements in the {@link CharSequence}
     * @param transform the transformation {@link Function} to apply on each element
     * @param <R>       the type of the resulting {@link List}
     * @return {@link List} of results of the transform applied to an each {@link CharSequence}
     */
    public static @NotNull <R> List<R> chunked(final @NotNull CharSequence seq, final @Range(from = 1, to = Integer.MAX_VALUE) int size,
                                               final @NotNull Function<? super CharSequence, ? extends R> transform) {
        return windowed(seq, size, size, true, transform);
    }

    /**
     * Returns the longest {@link String} prefix such that both {@link CharSequence}s getStart with this prefix, taking care not to split surrogate
     * pairs. If both have no common prefix, returns the empty {@link String}.
     *
     * @param first  the first {@link CharSequence}
     * @param second the second {@link CharSequence}
     * @return the longest {@link String} prefix such that both {@link CharSequence}s getStart with this prefix
     */
    public static @NotNull String commonPrefixWith(final @NotNull CharSequence first, final @NotNull CharSequence second) {
        return commonPrefixWith(first, second, false);
    }

    /**
     * Returns the longest {@link String} prefix such that both {@link CharSequence}s getStart with this prefix, ignoring character case when matching
     * a character if {@code ignoreCase} is {@code true}, taking care not to split surrogate pairs. If both have no common prefix, returns the empty
     * {@link String}.
     *
     * @param first      the first {@link CharSequence}
     * @param second     the second {@link CharSequence}
     * @param ignoreCase {@code true} to ignore character case, {@code false} otherwise
     * @return the longest {@link String} prefix such that both {@link CharSequence}s getStart with this prefix ignoring character case when matching
     * a character
     */
    public static @NotNull String commonPrefixWith(final @NotNull CharSequence first, final @NotNull CharSequence second, final boolean ignoreCase) {
        final int shortestLength = Math.min(first.length(), second.length());
        int i = 0;
        while (i < shortestLength && CharUtils.equals(first.charAt(i), second.charAt(i), ignoreCase)) {
            i++;
        }
        if (hasSurrogatePairAt(first, i - 1) || hasSurrogatePairAt(second, i - 1)) {
            i--;
        }
        return first.subSequence(0, i).toString();
    }

    /**
     * Returns the longest {@link String} suffix such that both {@link CharSequence}s end with this suffix, taking care not to split surrogate
     * pairs. If both have no common suffix, returns the empty {@link String}.
     *
     * @param first  the first {@link CharSequence}
     * @param second the second {@link CharSequence}
     * @return the longest {@link String} suffix such that both {@link CharSequence}s end with this suffix
     */
    public static @NotNull String commonSuffixWith(final @NotNull CharSequence first, final @NotNull CharSequence second) {
        return commonSuffixWith(first, second, false);
    }

    /**
     * Returns the longest {@link String} suffix such that both {@link CharSequence}s end with this suffix, ignoring character case when matching a
     * character if {@code ignoreCase} is {@code true}, taking care not to split surrogate pairs. If both have no common suffix, returns the empty
     * {@link String}.
     *
     * @param first      the first {@link CharSequence}
     * @param second     the second {@link CharSequence}
     * @param ignoreCase {@code true} to ignore character case, {@code false} otherwise
     * @return the longest {@link String} suffix such that both {@link CharSequence}s end with this suffix, ignoring
     * character case when matching a character
     */
    public static @NotNull String commonSuffixWith(final @NotNull CharSequence first, final @NotNull CharSequence second, final boolean ignoreCase) {
        final int firstLength = first.length();
        final int secondLength = second.length();
        final int shortestLength = Math.min(firstLength, secondLength);

        int i = 0;
        while (i < shortestLength && CharUtils.equals(first.charAt(firstLength - i - 1), second.charAt(secondLength - i - 1), ignoreCase)) {
            i++;
        }
        if (hasSurrogatePairAt(first, firstLength - i - 1) || hasSurrogatePairAt(second, secondLength - i - 1)) {
            i--;
        }
        return first.subSequence(firstLength - i, firstLength).toString();
    }

    public static boolean contains(final CharSequence first, final CharSequence second) {
        return contains(first, second, false);
    }

    public static boolean contains(final CharSequence first, final CharSequence second, final boolean ignoreCase) {
        return second instanceof String ?
            indexOf(first, (String) second, ignoreCase) >= 0 :
            indexOf(first, second, 0, first.length(), ignoreCase, false) >= 0;
    }

    public static boolean contains(final CharSequence seq, final char ch) {
        return contains(seq, ch, false);
    }

    public static boolean contains(final CharSequence seq, final char ch, final boolean ignoreCase) {
        return indexOf(seq, ch, ignoreCase) >= 0;
    }

    /**
     * Returns the length of the {@link CharSequence}.
     *
     * @param seq the {@link CharSequence}
     * @return the length of the {@link CharSequence}
     */
    public static @Range(from = 0, to = Integer.MAX_VALUE) int count(final @NotNull CharSequence seq) {
        return seq.length();
    }

    /**
     * Returns the number of characters in the {@link CharSequence} matching the given {@link Predicate}.
     *
     * @param seq       the {@link CharSequence}
     * @param predicate the {@link Predicate}
     * @return the number of characters in the {@link CharSequence} that satisfy the given {@link Predicate}
     */
    public static @Range(from = 0, to = Integer.MAX_VALUE) int count(final @NotNull CharSequence seq,
                                                                     final @NotNull Predicate<? super Character> predicate) {
        int count = 0;
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns a the {@link String} having its first letter lowercased, or the original {@link String}, if it's empty or already starts with a
     * lower case letter.
     *
     * @param str the {@link String}
     * @return the {@link String} having its first letter lowercased
     */
    @NonNls
    public static @NotNull String decapitalize(final @NotNull String str) {
        return isNotEmpty(str) && isUpperCase(str.charAt(0)) ? str.substring(0, 1).toLowerCase() + str.substring(1) : str;
    }

    /**
     * Returns a subsequence of the {@link CharSequence} with the first {@code n} characters removed.
     *
     * @param seq the {@link CharSequence}
     * @param n   the number of characters to remove. Must be a non-negative integer
     * @return the {@link CharSequence} with the first {@code n} characters removed
     */
    public static @NotNull CharSequence drop(final @NotNull CharSequence seq, final @Range(from = 0, to = Integer.MAX_VALUE) int n) {
        final int length = seq.length();
        return seq.subSequence(coerceAtMost(n, length), length);
    }

    /**
     * Returns a {@link String} with the first {@code n} characters removed.
     *
     * @param str the {@link String}
     * @param n   the number of characters to remove. Must be a non-negative integer
     * @return the {@link String} with the first {@code n} characters removed
     */
    public static @NotNull String drop(final @NotNull String str, final @Range(from = 0, to = Integer.MAX_VALUE) int n) {
        return str.substring(coerceAtMost(n, str.length()));
    }

    /**
     * Returns a subsequence of the {@link CharSequence} with the last {@code n} characters removed.
     *
     * @param seq the {@link CharSequence}
     * @param n   the number of characters to remove. Must be a non-negative integer
     * @return the {@link CharSequence} with the last {@code n} characters removed
     */
    public static @NotNull CharSequence dropLast(final @NotNull CharSequence seq, final @Range(from = 0, to = Integer.MAX_VALUE) int n) {
        return take(seq, coerceAtLeast(seq.length() - n, 0));
    }

    /**
     * Returns a {@link String} with the last {@code n} characters removed.
     *
     * @param str the {@link String}
     * @param n   the number of characters to remove. Must be a non-negative integer
     * @return the {@link String} with the last {@code n} characters removed.
     */
    public static @NotNull String dropLast(final @NotNull String str, final @Range(from = 0, to = Integer.MAX_VALUE) int n) {
        return take(str, coerceAtLeast(str.length() - n, 0));
    }

    /**
     * Returns a subsequence of the {@link CharSequence} containing all characters except last characters that satisfy the given {@link Predicate}.
     *
     * @param seq       the {@link CharSequence}
     * @param predicate the {@link Predicate}
     * @return the {@link CharSequence} containing all characters except last characters that satisfy the given {@link Predicate}.
     */
    public static @NotNull CharSequence dropLastWhile(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        final Predicate<? super Character> negated = predicate.negate();
        for (int i = lastIndex(seq); i >= 0; i--) {
            final char element = seq.charAt(i);
            if (negated.test(element)) {
                return seq.subSequence(0, i + 1);
            }
        }
        return "";
    }

    /**
     * Returns a {@link String} containing all characters except last characters that satisfy the given {@link Predicate}.
     *
     * @param str       the {@link String}
     * @param predicate the {@link Predicate}
     * @return a {@link String} containing all characters except last characters that satisfy the given @link Predicate}.
     */
    public static @NotNull String dropLastWhile(final @NotNull String str, final @NotNull Predicate<? super Character> predicate) {
        final Predicate<? super Character> negated = predicate.negate();
        for (int i = lastIndex(str); i >= 0; i--) {
            final char element = str.charAt(i);
            if (negated.test(element)) {
                return str.substring(0, i + 1);
            }
        }
        return "";
    }

    /**
     * Returns a subsequence of the {@link CharSequence} containing all characters except first characters that satisfy the given {@link Predicate}.
     *
     * @param seq       the {@link CharSequence}
     * @param predicate the {@link Predicate}
     * @return the {@link CharSequence} containing all characters except first characters that satisfy the given {@link Predicate}
     */
    public static @NotNull CharSequence dropWhile(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        final int length = seq.length();
        final Predicate<? super Character> negated = predicate.negate();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (negated.test(element)) {
                return seq.subSequence(i, length);
            }
        }
        return "";
    }

    /**
     * Returns a {@link String} containing all characters except first characters that satisfy the given {@link Predicate}.
     *
     * @param str       the {@link CharSequence}
     * @param predicate the {@link Predicate}
     * @return a {@link String} containing all characters except first characters that satisfy the given {@link Predicate}.
     */
    public static @NotNull String dropWhile(final @NotNull String str, final @NotNull Predicate<? super Character> predicate) {
        final int length = str.length();
        final Predicate<? super Character> negated = predicate.negate();
        for (int i = 0; i < length; i++) {
            final char element = str.charAt(i);
            if (negated.test(element)) {
                return str.substring(i);
            }
        }
        return "";
    }

    /**
     * Returns a character at the given {@code index} or the result of calling the defaultValue {@link Function}if the {@code index} is out of
     * bounds of the {@link CharSequence}.
     *
     * @param seq          the {@link CharSequence}
     * @param index        the {@code index}
     * @param defaultValue the {@link Function} to invoke if the {@code index} is out of bounds
     * @return character at the given {@code index} or the defaultValue
     */
    public static char elementAtOrElse(final @NotNull CharSequence seq, final int index,
                                       final @NotNull Function<? super Integer, Character> defaultValue) {
        return getOrElse(seq, index, defaultValue);
    }

    /**
     * Returns the {@link Character} at the given index or {@code null} if the index is out of bounds of the {@link CharSequence}.
     *
     * @param seq   the {@link CharSequence}
     * @param index the {@code index} to lookup
     * @return the {@link Character} at the given index or {@code null} if the index is out of bounds
     */
    public static @Nullable Character elementAtOrNull(final @NotNull CharSequence seq, final int index) {
        return getOrNull(seq, index);
    }

    /**
     * Returns {@code true} if this string ends with the specified suffix.
     *
     * @param str    the {@link String}
     * @param suffix the suffix {@link String}
     * @return {@code true} if this string ends with the specified suffix, {@code false} otherwise
     */
    @Contract(pure = true)
    public static boolean endsWith(final @NotNull String str, final @NotNull String suffix) {
        return endsWith(str, suffix, false);
    }

    /**
     * Returns {@code true} if this string ends with the specified suffix ignoring the case.
     *
     * @param str        the {@link String}
     * @param suffix     the suffix {@link String}
     * @param ignoreCase {@code true} if character case needs to be ignored, {@code false} otherwise
     * @return {@code true} if this string ends with the specified suffix ignoring the case, {@code false} otherwise
     */
    public static boolean endsWith(final @NotNull String str, final @NotNull String suffix, final boolean ignoreCase) {
        if (ignoreCase) {
            final int strLength = str.length();
            final int suffixLength = suffix.length();

            return regionMatches(str, strLength - suffixLength, suffix, 0, suffixLength, true);
        } else {
            return str.endsWith(suffix);
        }
    }

    // Returns true if this char sequence ends with the specified character.
    public static boolean endsWith(final @NotNull CharSequence seq, final char ch) {
        return endsWith(seq, ch, false);
    }

    // Returns true if this char sequence ends with the specified character ignoring the case.
    public static boolean endsWith(final @NotNull CharSequence seq, final char ch, final boolean ignoreCase) {
        final int length = seq.length();
        final int lastIndex = lastIndex(seq);

        return length > 0 && CharUtils.equals(seq.charAt(lastIndex), ch, ignoreCase);
    }

    // Returns true if this char sequence ends with the specified suffix.
    public static boolean endsWith(final @NotNull CharSequence seq, final @NotNull CharSequence suffix) {
        return endsWith(seq, suffix, false);
    }

    // Returns true if this char sequence ends with the specified suffix ignoring the case.
    public static boolean endsWith(final @NotNull CharSequence seq, final @NotNull CharSequence suffix, final boolean ignoreCase) {
        if (!ignoreCase && seq instanceof String && suffix instanceof String) {
            return ((String) seq).endsWith((String) suffix);
        } else {
            final int length = seq.length();
            final int suffixLength = suffix.length();

            return regionMatchesImpl(seq, length - suffixLength, suffix, 0, suffixLength, ignoreCase);
        }
    }

    // Returns a char sequence containing only those characters from the original char sequence that match the given predicate.
    public static @NotNull CharSequence filter(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        return filterTo(seq, new StringBuilder(), predicate);
    }

    // Returns a string containing only those characters from the original string that match the given predicate.
    public static @NotNull String filter(final @NotNull String str, final @NotNull Predicate<? super Character> predicate) {
        return filterTo(str, new StringBuilder(), predicate).toString();
    }

    // Returns a char sequence containing only those characters from the original char sequence that match the given predicate.
    public static @NotNull CharSequence filterIndexed(final @NotNull CharSequence seq,
                                                      final @NotNull BiPredicate<? super Integer, ? super Character> predicate) {
        return filterIndexedTo(seq, new StringBuilder(), predicate);
    }

    // Returns a string containing only those characters from the original string that match the given predicate.
    public static @NotNull String filterIndexed(final @NotNull String str, final @NotNull BiPredicate<? super Integer, ? super Character> predicate) {
        return filterIndexedTo(str, new StringBuilder(), predicate).toString();
    }

    // Appends all characters matching the given predicate to the given destination.
    @Contract("_, _, _ -> param2")
    public static @NotNull <C extends Appendable> C filterIndexedTo(final @NotNull CharSequence seq, final @NotNull C destination,
                                                                    final @NotNull BiPredicate<? super Integer, ? super Character> predicate) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (predicate.test(i, element)) {
                try {
                    destination.append(element);
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        return destination;
    }

    // Returns a char sequence containing only those characters from the original char sequence that do not match the given predicate.
    public static @NotNull CharSequence filterNot(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        return filterNotTo(seq, new StringBuilder(), predicate);
    }

    // Returns a string containing only those characters from the original string that do not match the given predicate.
    public static @NotNull String filterNot(final @NotNull String str, final @NotNull Predicate<? super Character> predicate) {
        return filterNotTo(str, new StringBuilder(), predicate).toString();
    }

    // Appends all characters not matching the given predicate to the given destination.
    @Contract("_, _, _ -> param2")
    public static @NotNull <C extends Appendable> C filterNotTo(final @NotNull CharSequence seq, final @NotNull C destination,
                                                                final @NotNull Predicate<? super Character> predicate) {
        return filterTo(seq, destination, predicate.negate());
    }

    // Appends all characters matching the given predicate to the given destination.
    @Contract("_, _, _ -> param2")
    public static @NotNull <C extends Appendable> C filterTo(final @NotNull CharSequence seq, final @NotNull C destination,
                                                             final @NotNull Predicate<? super Character> predicate) {
        final int length = seq.length();
        return filterTo(seq, destination, length, predicate);
    }

    // Returns the first character matching the given predicate, or empty() if no such character was found.
    public static @NotNull Optional<Character> find(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        return ofNullable(firstOrNull(seq, predicate));
    }

    public static @NotNull Optional<Pair<Integer, String>> findAnyOf(final @NotNull CharSequence seq, final @NotNull Collection<String> strings) {
        return findAnyOf(seq, strings, 0, false);
    }

    public static @NotNull Optional<Pair<Integer, String>> findAnyOf(final @NotNull CharSequence seq, final @NotNull Collection<String> strings,
                                                                     final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex) {
        return findAnyOf(seq, strings, startIndex, false);
    }

    public static @NotNull Optional<Pair<Integer, String>> findAnyOf(final @NotNull CharSequence seq, final @NotNull Collection<String> strings,
                                                                     final boolean ignoreCase) {
        return findAnyOf(seq, strings, 0, ignoreCase);
    }

    // Finds the first occurrence of any of the specified strings in the char sequence, starting from the specified startIndex and optionally
    // ignoring the case.
    public static @NotNull Optional<Pair<Integer, String>> findAnyOf(final @NotNull CharSequence seq,
                                                                     final @NotNull Collection<String> strings,
                                                                     final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex,
                                                                     final boolean ignoreCase) {
        return findAnyOf(seq, strings, startIndex, ignoreCase, false);
    }

    // Returns the last character matching the given predicate, or Optional#empty if no such character was found.
    public static @NotNull Optional<Character> findLast(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        return ofNullable(lastOrNull(seq, predicate));
    }

    public static @NotNull Optional<Pair<Integer, String>> findLastAnyOf(final @NotNull CharSequence seq, final @NotNull Collection<String> strings) {
        return findLastAnyOf(seq, strings, lastIndex(seq), false);
    }

    public static @NotNull Optional<Pair<Integer, String>> findLastAnyOf(final @NotNull CharSequence seq,
                                                                         final @NotNull Collection<String> strings,
                                                                         final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex) {
        return findLastAnyOf(seq, strings, startIndex, false);
    }

    public static @NotNull Optional<Pair<Integer, String>> findLastAnyOf(final @NotNull CharSequence seq, final @NotNull Collection<String> strings,
                                                                         final boolean ignoreCase) {
        return findLastAnyOf(seq, strings, lastIndex(seq), ignoreCase);
    }

    // Finds the last occurrence of any of the specified strings in the char sequence, starting from the specified startIndex and optionally ignoring
    // the case.
    public static @NotNull Optional<Pair<Integer, String>> findLastAnyOf(final @NotNull CharSequence seq,
                                                                         final @NotNull Collection<String> strings,
                                                                         final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex,
                                                                         final boolean ignoreCase) {
        return findAnyOf(seq, strings, startIndex, ignoreCase, true);
    }

    // Returns first character.
    public static char first(final @NotNull CharSequence seq) {
        if (isEmpty(seq)) {
            throw new NoSuchElementException("Char sequence is empty.");
        }
        return seq.charAt(0);
    }

    // Returns the first character matching the given predicate.
    public static char first(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                return element;
            }
        }
        throw new NoSuchElementException("Char sequence contains no character matching the predicate.");
    }

    // Returns the first character, or null if the char sequence is empty.
    public static @Nullable Character firstOrNull(final @NotNull CharSequence seq) {
        return isEmpty(seq) ? null : seq.charAt(0);
    }

    // Returns the first character matching the given predicate, or null if character was not found.
    public static @Nullable Character firstOrNull(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                return element;
            }
        }
        return null;
    }

    // Returns a single list of all elements yielded from results of transform function being invoked on each character of original char sequence.
    public static @NotNull <R> List<R> flatMap(final @NotNull CharSequence seq,
                                               final @NotNull Function<? super Character, ? extends Iterable<R>> transform) {
        return flatMapTo(seq, new ArrayList<>(), transform);
    }

    // Appends all elements yielded from results of transform function being invoked on each character of original char sequence, to the given
    // destination.
    @Contract("_, _, _ -> param2")
    public static @NotNull <R, C extends Collection<? super R>> C flatMapTo(final @NotNull CharSequence seq, final @NotNull C destination,
                                                                            final @NotNull Function<? super Character, ? extends Iterable<R>> transform) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            final Iterable<R> list = transform.apply(element);
            CollectionUtils.addAll(destination, list);
        }
        return destination;
    }

    // Accumulates value starting with initial value and applying operation from left to right to current accumulator value and each character.
    public static @NotNull <R> R fold(final @NotNull CharSequence seq, final @NotNull R initial,
                                      final @NotNull BiFunction<? super R, ? super Character, ? extends R> operation) {
        R accumulator = initial;
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            accumulator = operation.apply(accumulator, element);
        }
        return accumulator;
    }

    // Accumulates value starting with initial value and applying operation from left to right to current accumulator value and each character with
    // its index in the original char sequence.
    public static @NotNull <R> R foldIndexed(final @NotNull CharSequence seq, final @NotNull R initial,
                                             final @NotNull TriFunction<? super Integer, ? super R, ? super Character, ? extends R> operation) {
        R accumulator = initial;
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            accumulator = operation.apply(i, accumulator, element);
        }
        return accumulator;
    }

    // Accumulates value starting with initial value and applying operation from right to left to each character and current accumulator value.
    public static @NotNull <R> R foldRight(final @NotNull CharSequence seq, final @NotNull R initial,
                                           final @NotNull BiFunction<? super Character, ? super R, ? extends R> operation) {
        int index = lastIndex(seq);
        R accumulator = initial;
        while (index >= 0) {
            accumulator = operation.apply(seq.charAt(index--), accumulator);
        }
        return accumulator;
    }

    // Accumulates value starting with initial value and applying operation from right to left to each character with its index in the original char
    // sequence and current accumulator value.
    public static @NotNull <R> R foldRightIndexed(final @NotNull CharSequence seq, final @NotNull R initial,
                                                  final @NotNull TriFunction<? super Integer, ? super Character, ? super R, ? extends R> operation) {
        int index = lastIndex(seq);
        R accumulator = initial;
        while (index >= 0) {
            accumulator = operation.apply(index, seq.charAt(index), accumulator);
            --index;
        }
        return accumulator;
    }

    // Performs the given action on each character.
    public static void forEach(final @NotNull CharSequence seq, final @NotNull Consumer<? super Character> action) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            action.accept(element);
        }
    }

    // Performs the given action on each character, providing sequential index with the character.
    public static void forEachIndexed(final @NotNull CharSequence seq, final @NotNull BiConsumer<? super Integer, ? super Character> action) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            action.accept(i, element);
        }
    }

    // Returns a character at the given index or the result of calling the defaultValue function if the index is out of bounds of this char sequence.
    public static @NotNull Character getOrElse(final @NotNull CharSequence seq, final int index,
                                               final @NotNull Function<? super Integer, Character> defaultValue) {
        return index >= 0 && index <= lastIndex(seq) ? seq.charAt(index) : defaultValue.apply(index);
    }

    // Returns a character at the given index or null if the index is out of bounds of this char sequence.
    public static @Nullable Character getOrNull(final @NotNull CharSequence seq, final int index) {
        return index >= 0 && index <= lastIndex(seq) ? seq.charAt(index) : null;
    }

    // Groups characters of the original char sequence by the key returned by the given keySelector function applied to each character and returns a
    // map where each group key is associated with a list of corresponding characters.
    // The returned map preserves the entry iteration order of the keys produced from the original char sequence.
    public static @NotNull <K> Map<K, List<Character>> groupBy(final @NotNull CharSequence seq,
                                                               final @NotNull Function<? super Character, K> keySelector) {
        return groupByTo(seq, new LinkedHashMap<>(), keySelector);
    }

    // Groups values returned by the valueTransform function applied to each character of the original char sequence by the key returned by the given
    // keySelector function applied to the character and returns a map where each group key is associated with a list of corresponding values.
    //
    // The returned map preserves the entry iteration order of the keys produced from the original char sequence.
    public static @NotNull <K, V> Map<K, List<V>> groupBy(final @NotNull CharSequence seq, final @NotNull Function<? super Character, K> keySelector,
                                                          final @NotNull Function<? super Character, V> valueTransform) {
        return groupByTo(seq, new LinkedHashMap<>(), keySelector, valueTransform);
    }

    // Groups characters of the original char sequence by the key returned by the given keySelector function applied to each character and puts to the
    // destination map each group key associated with a list of corresponding characters.
    @Contract("_, _, _ -> param2")
    public static @NotNull <K, M extends Map<K, List<Character>>> M groupByTo(final @NotNull CharSequence seq, final @NotNull M destination,
                                                                              final @NotNull Function<? super Character, K> keySelector) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            final K key = keySelector.apply(element);
            final List<Character> list = CollectionUtils.getOrPut(destination, key, ArrayList::new);
            list.add(element);
        }
        return destination;
    }

    // Groups values returned by the valueTransform function applied to each character of the original char sequence by the key returned by the given
    // keySelector function applied to the character and puts to the destination map each group key associated with a list of corresponding values.
    @Contract("_, _, _, _ -> param2")
    public static @NotNull <K, V, M extends Map<K, List<V>>> M groupByTo(final @NotNull CharSequence seq, final @NotNull M destination,
                                                                         final @NotNull Function<? super Character, K> keySelector,
                                                                         final @NotNull Function<? super Character, V> valueTransform) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            final K key = keySelector.apply(element);
            final List<V> list = CollectionUtils.getOrPut(destination, key, ArrayList::new);
            list.add(valueTransform.apply(element));
        }
        return destination;
    }

    // Returns true if this CharSequence has Unicode surrogate pair at the specified index.
    public static boolean hasSurrogatePairAt(final @NotNull CharSequence seq, final int index) {
        return index >= 0 &&
            index <= seq.length() - 2 &&
            isHighSurrogate(seq.charAt(index)) &&
            isLowSurrogate(seq.charAt(index + 1));
    }

    // Returns the char sequence if it is not empty and doesn't consist solely of whitespace characters, or the result of calling defaultValue
    // function otherwise.
    public static @NotNull CharSequence ifBlank(final @NotNull CharSequence seq, final @NotNull Supplier<? extends CharSequence> defaultValue) {
        return isBlank(seq) ? defaultValue.get() : seq;
    }

    // Returns the char sequence if it's not empty or the result of calling defaultValue function if the char sequence is empty.
    public static @NotNull CharSequence ifEmpty(final @NotNull CharSequence seq, final @NotNull Supplier<? extends CharSequence> defaultValue) {
        return isEmpty(seq) ? defaultValue.get() : seq;
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOf(final @NotNull CharSequence seq, final char ch) {
        return indexOf(seq, ch, 0, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOf(final @NotNull CharSequence seq, final char ch,
                                                                        final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex) {
        return indexOf(seq, ch, startIndex, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOf(final @NotNull CharSequence seq, final char ch, final boolean ignoreCase) {
        return indexOf(seq, ch, 0, ignoreCase);
    }

    // Returns the index within this string of the first occurrence of the specified character, starting from the specified startIndex.
    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOf(final @NotNull CharSequence seq, final char ch,
                                                                        final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex,
                                                                        final boolean ignoreCase) {
        return ignoreCase || !(seq instanceof String) ?
            indexOfAny(seq, new char[]{ch}, startIndex, ignoreCase) :
            ((String) seq).indexOf(ch, startIndex);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOf(final @NotNull CharSequence seq, final @NotNull String str) {
        return indexOf(seq, str, 0, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOf(final @NotNull CharSequence seq, final @NotNull String str,
                                                                        final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex) {
        return indexOf(seq, str, startIndex, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOf(final @NotNull CharSequence seq, final @NotNull String str,
                                                                        final boolean ignoreCase) {
        return indexOf(seq, str, 0, ignoreCase);
    }

    // Returns the index within this char sequence of the first occurrence of the specified string, starting from the specified startIndex.
    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOf(final @NotNull CharSequence seq, final @NotNull String str,
                                                                        final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex,
                                                                        final boolean ignoreCase) {
        return ignoreCase || !(seq instanceof String) ?
            indexOf(seq, str, startIndex, seq.length(), ignoreCase, false) :
            ((String) seq).indexOf(str, startIndex);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOfAny(final @NotNull CharSequence seq, final @NotNull char[] chars) {
        return indexOfAny(seq, chars, 0, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOfAny(final @NotNull CharSequence seq, final @NotNull char[] chars,
                                                                           final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex) {
        return indexOfAny(seq, chars, startIndex, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOfAny(final @NotNull CharSequence seq, final @NotNull char[] chars,
                                                                           final boolean ignoreCase) {
        return indexOfAny(seq, chars, 0, ignoreCase);
    }

    // Finds the index of the first occurrence of any of the specified chars in this char sequence, starting from the specified startIndex and
    // optionally ignoring the case.
    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOfAny(final @NotNull CharSequence seq, final @NotNull char[] chars,
                                                                           final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex,
                                                                           final boolean ignoreCase) {
        if (!ignoreCase && chars.length == 1 && seq instanceof String) {
            final char single = ArrayUtils.single(chars);
            return ((String) seq).indexOf(single, startIndex);
        }

        final int start = coerceAtLeast(startIndex, 0);
        final int lastIndex = lastIndex(seq);
        for (int i = start; i <= lastIndex; i++) {
            final char charAtIndex = seq.charAt(i);
            if (ArrayUtils.any(chars, ch -> CharUtils.equals(ch, charAtIndex, ignoreCase))) {
                return i;
            }
        }
        return -1;
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOfAny(final @NotNull CharSequence seq, final @NotNull Collection<String> strings) {
        return indexOfAny(seq, strings, 0, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOfAny(final @NotNull CharSequence seq, final @NotNull Collection<String> strings,
                                                                           final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex) {
        return indexOfAny(seq, strings, startIndex, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOfAny(final @NotNull CharSequence seq, final @NotNull Collection<String> strings,
                                                                           final boolean ignoreCase) {
        return indexOfAny(seq, strings, 0, ignoreCase);
    }

    // Finds the index of the first occurrence of any of the specified strings in this char sequence, starting from the specified startIndex and
    // optionally ignoring the case.
    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOfAny(final @NotNull CharSequence seq, final @NotNull Collection<String> strings,
                                                                           final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex,
                                                                           final boolean ignoreCase) {
        return findAnyOf(seq, strings, startIndex, ignoreCase, false)
            .map(Pair::getFirst)
            .orElse(-1);
    }

    // Returns index of the first character matching the given predicate, or -1 if the char sequence does not contain such character.
    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOfFirst(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                return i;
            }
        }
        return -1;
    }

    // Returns index of the last character matching the given predicate, or -1 if the char sequence does not contain such character.
    public static @Range(from = -1, to = Integer.MAX_VALUE) int indexOfLast(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        for (int i = lastIndex(seq); i >= 0; i--) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                return i;
            }
        }
        return -1;
    }

    // Returns true if this string is empty or consists solely of whitespace characters.
    public static boolean isBlank(final @NotNull CharSequence seq) {
        return isEmpty(seq) || CollectionUtils.all(indices(seq), i -> isWhitespace(seq.charAt(i)));
    }

    // Returns true if this char sequence is empty (contains no characters).
    public static boolean isEmpty(final @NotNull CharSequence seq) {
        return seq.length() == 0;
    }

    // Returns true if this char sequence is not empty and contains some characters except of whitespace characters.
    public static boolean isNotBlank(final @NotNull CharSequence seq) {
        return !isBlank(seq);
    }

    // Returns true if this char sequence is not empty.
    public static boolean isNotEmpty(final @NotNull CharSequence seq) {
        return seq.length() > 0;
    }

    // Returns true if this nullable char sequence is either null or empty or consists solely of whitespace characters.
    @Contract("null -> true")
    public static boolean isNullOrBlank(final @Nullable CharSequence seq) {
        return seq == null || isBlank(seq);
    }

    // Returns true if this nullable char sequence is either null or empty.
    @Contract("null -> true")
    public static boolean isNullOrEmpty(final @Nullable CharSequence seq) {
        return seq == null || isEmpty(seq);
    }

    // Iterator for characters of the given char sequence.
    @Contract("_ -> new")
    public static @NotNull CharIterator iterator(final @NotNull CharSequence seq) {
        return new CharIterator() {
            private int index;
            private final int length = seq.length();

            @Override
            public char nextChar() {
                return seq.charAt(index++);
            }

            @Override
            public boolean hasNext() {
                return index < length;
            }
        };
    }

    // Returns the last character.
    public static char last(final @NotNull CharSequence seq) {
        if (isEmpty(seq)) {
            throw new NoSuchElementException("Char sequence is empty.");
        }
        return seq.charAt(lastIndex(seq));
    }

    // Returns the last character matching the given predicate.
    public static char last(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        for (int i = lastIndex(seq); i >= 0; i--) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                return element;
            }
        }
        throw new NoSuchElementException("Char sequence contains no character matching the predicate.");
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOf(final @NotNull CharSequence seq, final char ch) {
        return lastIndexOf(seq, ch, lastIndex(seq), false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOf(final @NotNull CharSequence seq, final char ch,
                                                                            final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex) {
        return lastIndexOf(seq, ch, startIndex, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOf(final @NotNull CharSequence seq, final char ch,
                                                                            final boolean ignoreCase) {
        return lastIndexOf(seq, ch, lastIndex(seq), ignoreCase);
    }

    // Returns the index within this char sequence of the last occurrence of the specified character, starting from the specified startIndex.
    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOf(final @NotNull CharSequence seq, final char ch,
                                                                            final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex,
                                                                            final boolean ignoreCase) {
        return ignoreCase || !(seq instanceof String) ?
            lastIndexOfAny(seq, new char[]{ch}, startIndex, ignoreCase) :
            ((String) seq).lastIndexOf(ch, startIndex);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOf(final @NotNull CharSequence seq, final @NotNull String str) {
        return lastIndexOf(seq, str, lastIndex(seq), false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOf(final @NotNull CharSequence seq, final @NotNull String str,
                                                                            final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex) {
        return lastIndexOf(seq, str, startIndex, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOf(final @NotNull CharSequence seq, final @NotNull String str,
                                                                            final boolean ignoreCase) {
        return lastIndexOf(seq, str, lastIndex(seq), ignoreCase);
    }

    // Returns the index within this char sequence of the last occurrence of the specified string, starting from the specified startIndex.
    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOf(final @NotNull CharSequence seq, final @NotNull String str,
                                                                            final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex,
                                                                            final boolean ignoreCase) {
        return ignoreCase || !(seq instanceof String) ?
            indexOf(seq, str, startIndex, 0, ignoreCase, true) :
            ((String) seq).lastIndexOf(str, startIndex);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOfAny(final @NotNull CharSequence seq, final @NotNull char[] chars) {
        return lastIndexOfAny(seq, chars, lastIndex(seq), false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOfAny(final @NotNull CharSequence seq, final @NotNull char[] chars,
                                                                               final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex) {
        return lastIndexOfAny(seq, chars, startIndex, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOfAny(final @NotNull CharSequence seq, final @NotNull char[] chars,
                                                                               final boolean ignoreCase) {
        return lastIndexOfAny(seq, chars, lastIndex(seq), ignoreCase);
    }

    // Finds the index of the last occurrence of any of the specified chars in this char sequence, starting from the specified startIndex and
    // optionally ignoring the case.
    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOfAny(final @NotNull CharSequence seq, final @NotNull char[] chars,
                                                                               final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex,
                                                                               final boolean ignoreCase) {
        if (!ignoreCase && chars.length == 1 && seq instanceof String) {
            final char ch = ArrayUtils.single(chars);
            return ((String) seq).lastIndexOf(ch, startIndex);
        }
        final int start = coerceAtMost(startIndex, lastIndex(seq));
        for (int i = start; i >= 0; i--) {
            final char charAtIndex = seq.charAt(i);
            if (ArrayUtils.any(chars, ch -> CharUtils.equals(ch, charAtIndex, ignoreCase))) {
                return i;
            }
        }
        return -1;
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOfAny(final @NotNull CharSequence seq, final @NotNull Collection<String> strings) {
        return lastIndexOfAny(seq, strings, lastIndex(seq), false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOfAny(final @NotNull CharSequence seq, final @NotNull Collection<String> strings,
                                                                               final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex) {
        return lastIndexOfAny(seq, strings, startIndex, false);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOfAny(final @NotNull CharSequence seq, final @NotNull Collection<String> strings,
                                                                               final boolean ignoreCase) {
        return lastIndexOfAny(seq, strings, lastIndex(seq), ignoreCase);
    }

    public static @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOfAny(final @NotNull CharSequence seq, final @NotNull Collection<String> strings,
                                                                               final @Range(from = 0, to = Integer.MAX_VALUE) int startIndex,
                                                                               final boolean ignoreCase) {
        return findAnyOf(seq, strings, startIndex, ignoreCase, true)
            .map(Pair::getFirst)
            .orElse(-1);
    }

    // Returns the last character, or null if the char sequence is empty.
    public static @Nullable Character lastOrNull(final @NotNull CharSequence seq) {
        return isEmpty(seq) ? null : seq.charAt(lastIndex(seq));
    }

    // Returns the last character matching the given predicate, or null if no such character was found.
    public static @Nullable Character lastOrNull(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        for (int i = lastIndex(seq); i >= 0; i--) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                return element;
            }
        }
        return null;
    }

    // Splits this char sequence to a list of lines delimited by any of the following character sequences: CRLF, LF or CR.
    // The lines returned do not include terminating line separators.
    public static @NotNull List<String> lines(final @NotNull CharSequence seq) {
        return SequenceUtils.toList(lineSequence(seq));
    }

    // Splits this char sequence to a sequence of lines delimited by any of the following character sequences: CRLF, LF or CR.
    // The lines returned do not include terminating line separators.
    public static @NotNull Sequence<String> lineSequence(final @NotNull CharSequence seq) {
        return splitToSequence(seq, Arrays.asList("\r\n", "\n", "\r"));
    }

    // Returns a list containing the results of applying the given transform function to each character in the original char sequence.
    public static @NotNull <R> List<R> map(final @NotNull CharSequence seq, final @NotNull Function<? super Character, R> transform) {
        return mapTo(seq, new ArrayList<>(seq.length()), transform);
    }

    // Returns a list containing the results of applying the given transform function to each character and its index in the original char sequence.
    public static @NotNull <R> List<R> mapIndexed(final @NotNull CharSequence seq,
                                                  final @NotNull BiFunction<? super Integer, ? super Character, R> transform) {
        return mapIndexedTo(seq, new ArrayList<>(seq.length()), transform);
    }

    // Returns a list containing only the non-null results of applying the given transform function to each character and its index in the original
    // char sequence.
    public static @NotNull <R> List<R> mapIndexedNotNull(final @NotNull CharSequence seq,
                                                         final @NotNull BiFunction<? super Integer, ? super Character, R> transform) {
        return mapIndexedNotNullTo(seq, new ArrayList<>(), transform);
    }

    // Applies the given transform function to each character and its index in the original char sequence and appends only the non-null results to
    // the given destination.
    @Contract("_, _, _ -> param2")
    public static @NotNull <R, C extends Collection<R>> C mapIndexedNotNullTo(final @NotNull CharSequence seq, final @NotNull C destination,
                                                                              final @NotNull BiFunction<? super Integer, ? super Character, @Nullable R> transform) {

        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            final R result = transform.apply(i, element);
            if (result != null) {
                destination.add(result);
            }
        }
        return destination;
    }

    // Applies the given transform function to each character and its index in the original char sequence and appends the results to the given destination.
    @Contract("_, _, _ -> param2")
    public static @NotNull <R, C extends Collection<R>> C mapIndexedTo(final @NotNull CharSequence seq, final @NotNull C destination,
                                                                       final @NotNull BiFunction<? super Integer, ? super Character, R> transform) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            destination.add(transform.apply(i, element));
        }
        return destination;
    }

    // Returns a list containing only the non-null results of applying the given transform function to each character in the original char sequence.
    public static @NotNull <R> List<R> mapNotNull(final @NotNull CharSequence seq, final @NotNull Function<? super Character, @Nullable R> transform) {
        return mapNotNullTo(seq, new ArrayList<>(), transform);
    }

    // Applies the given transform function to each character in the original char sequence and appends only the non-null results to the given destination.
    @Contract("_, _, _ -> param2")
    public static @NotNull <R, C extends Collection<R>> C mapNotNullTo(final @NotNull CharSequence seq, final @NotNull C destination,
                                                                       final @NotNull Function<? super Character, @Nullable R> transform) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            final R result = transform.apply(element);
            if (result != null) {
                destination.add(result);
            }
        }
        return destination;
    }

    // Applies the given transform function to each character of the original char sequence and appends the results to the given destination.
    @Contract("_, _, _ -> param2")
    public static @NotNull <R, C extends Collection<R>> C mapTo(final @NotNull CharSequence seq, final @NotNull C destination,
                                                                final @NotNull Function<? super Character, R> transform) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            destination.add(transform.apply(element));
        }
        return destination;
    }

    // Returns the largest character or Optional#empty if there are no characters.
    public static @NotNull Optional<Character> max(final @NotNull CharSequence seq) {
        return maxWith(seq, Character::compareTo);
    }

    // Returns the first character yielding the largest value of the given function or Optional#empty if there are no characters.
    public static @NotNull <R extends Comparable<R>> Optional<Character> maxBy(final @NotNull CharSequence seq,
                                                                               final @NotNull Function<? super Character, R> selector) {
        if (isEmpty(seq)) {
            return empty();
        }
        char maxElem = seq.charAt(0);
        R maxValue = selector.apply(maxElem);
        final int lastIndex = lastIndex(seq);
        for (int i = 1; i <= lastIndex; i++) {
            final char element = seq.charAt(i);
            final R value = selector.apply(element);
            if (maxValue.compareTo(value) < 0) {
                maxElem = element;
                maxValue = value;
            }
        }
        return of(maxElem);
    }

    // Returns the first character having the largest value according to the provided comparator or Optional#empty if there are no characters.
    public static @NotNull Optional<Character> maxWith(final @NotNull CharSequence seq, final @NotNull Comparator<? super Character> comparator) {
        if (isEmpty(seq)) {
            return empty();
        }
        char max = seq.charAt(0);
        final int lastIndex = lastIndex(seq);
        for (int i = 1; i <= lastIndex; i++) {
            final char element = seq.charAt(i);
            if (comparator.compare(max, element) < 0) {
                max = element;
            }
        }
        return of(max);
    }

    // Returns the smallest character or Optional#empty if there are no characters.
    public static @NotNull Optional<Character> min(final @NotNull CharSequence seq) {
        return minWith(seq, Character::compareTo);
    }

    // Returns the first character yielding the smallest value of the given function or Optional#empty if there are no characters.
    public static @NotNull <R extends Comparable<R>> Optional<Character> minBy(final @NotNull CharSequence seq,
                                                                               final @NotNull Function<? super Character, R> selector) {
        if (isEmpty(seq)) {
            return empty();
        }
        char minElem = seq.charAt(0);
        R minValue = selector.apply(minElem);
        final int lastIndex = lastIndex(seq);
        for (int i = 1; i <= lastIndex; i++) {
            final char element = seq.charAt(i);
            final R value = selector.apply(element);
            if (minValue.compareTo(value) > 0) {
                minElem = element;
                minValue = value;
            }
        }
        return of(minElem);
    }

    // Returns the first character having the smallest value according to the provided comparator or Optional#empty if there are no characters.
    public static @NotNull Optional<Character> minWith(final @NotNull CharSequence seq, final @NotNull Comparator<? super Character> comparator) {
        if (isEmpty(seq)) {
            return empty();
        }
        char min = seq.charAt(0);
        final int lastIndex = lastIndex(seq);
        for (int i = 1; i <= lastIndex; i++) {
            final char element = seq.charAt(i);
            if (comparator.compare(min, element) > 0) {
                min = element;
            }
        }
        return of(min);
    }

    // Returns true if the char sequence has no characters.
    public static boolean none(final @NotNull CharSequence seq) {
        return isEmpty(seq);
    }

    // Returns true if no characters match the given predicate.
    public static boolean none(final @NotNull CharSequence seq, final Predicate<? super Character> predicate) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                return false;
            }
        }
        return true;
    }

    // Performs the given action on each character and returns the char sequence itself afterwards.
    @Contract("_, _ -> param1")
    public static @NotNull <S extends CharSequence> S onEach(final @NotNull S seq, final @NotNull Consumer<? super Character> action) {
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            action.accept(element);
        }
        return seq;
    }

    // Returns the string if it is not null, or the empty string otherwise.
    @Contract(value = "!null -> param1", pure = true)
    public static @NotNull String orEmpty(final @Nullable String str) {
        return str != null ? str : "";
    }

    public static @NotNull CharSequence padEnd(final @NotNull CharSequence seq, final @Range(from = 0, to = Integer.MAX_VALUE) int length) {
        return padEnd(seq, length, ' ');
    }

    // Returns a char sequence with content of this char sequence padded at the end to the specified length with the specified character or space.
    public static @NotNull CharSequence padEnd(final @NotNull CharSequence seq, final @Range(from = 0, to = Integer.MAX_VALUE) int length,
                                               final char padChar) {
        final int seqLength = seq.length();
        if (length <= seqLength) {
            return seq.subSequence(0, seqLength);
        }

        final StringBuilder sb = new StringBuilder(length);
        sb.append(seq);

        final int end = length - seqLength;
        for (int i = 1; i <= end; i++) {
            sb.append(padChar);
        }
        return sb;
    }

    public static @NotNull String padEnd(final @NotNull String str, final @Range(from = 0, to = Integer.MAX_VALUE) int length) {
        return padEnd(str, length, ' ');
    }

    // Pads the string to the specified length at the end with the specified character or space.
    public static @NotNull String padEnd(final @NotNull String str, final @Range(from = 0, to = Integer.MAX_VALUE) int length,
                                         final char padChar) {
        return padEnd((CharSequence) str, length, padChar).toString();
    }

    public static @NotNull CharSequence padStart(final @NotNull CharSequence seq, final @Range(from = 0, to = Integer.MAX_VALUE) int length) {
        return padStart(seq, length, ' ');
    }

    // Returns a char sequence with content of this char sequence padded at the beginning to the specified length with the specified character or space.
    public static @NotNull CharSequence padStart(final @NotNull CharSequence seq, final @Range(from = 0, to = Integer.MAX_VALUE) int length,
                                                 final char padChar) {
        final int seqLength = seq.length();
        if (length <= seqLength) {
            return seq.subSequence(0, seqLength);
        }

        final StringBuilder sb = new StringBuilder(length);
        final int end = length - seqLength;
        for (int i = 1; i <= end; i++) {
            sb.append(padChar);
        }
        sb.append(seq);
        return sb;
    }

    public static @NotNull String padStart(final @NotNull String str, final @Range(from = 0, to = Integer.MAX_VALUE) int length) {
        return padStart(str, length, ' ');
    }

    // Pads the string to the specified length at the beginning with the specified character or space.
    public static @NotNull String padStart(final @NotNull String str, final @Range(from = 0, to = Integer.MAX_VALUE) int length,
                                           final char padChar) {
        return padStart((CharSequence) str, length, padChar).toString();
    }

    // Splits the original char sequence into pair of char sequences, where first char sequence contains characters for which predicate yielded true, while second char
    // sequence contains characters for which predicate yielded false.
    @Contract("_, _ -> new")
    public static @NotNull Pair<CharSequence, CharSequence> partition(final @NotNull CharSequence seq,
                                                                      final @NotNull Predicate<? super Character> predicate) {
        final StringBuilder first = new StringBuilder();
        final StringBuilder second = new StringBuilder();

        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                first.append(element);
            } else {
                second.append(element);
            }
        }
        return new Pair<>(first, second);
    }

    // Splits the original string into pair of strings, where first string contains characters for which predicate yielded true, while second string contains characters
    // for which predicate yielded false.
    @Contract("_, _ -> new")
    public static @NotNull Pair<String, String> partition(final @NotNull String str, final @NotNull Predicate<? super Character> predicate) {
        final StringBuilder first = new StringBuilder();
        final StringBuilder second = new StringBuilder();

        final int length = str.length();
        for (int i = 0; i < length; i++) {
            final char element = str.charAt(i);
            if (predicate.test(element)) {
                first.append(element);
            } else {
                second.append(element);
            }
        }
        return new Pair<>(first.toString(), second.toString());
    }

    public static @NotNull String prependIndent(final @NotNull String str) {
        return prependIndent(str, "    ");
    }

    // Prepends indent to every line of the original string. Doesn't preserve the original line endings.
    @NonNls
    public static @NotNull String prependIndent(final @NotNull String str, final @NotNull String indent) {
        final Sequence<String> sequence = lineSequence(str);
        final Sequence<String> mappedSeq = SequenceUtils.map(sequence, line -> {
            if (isBlank(line)) {
                return line.length() < indent.length() ? indent : line;
            } else {
                return indent + line;
            }
        });
        return SequenceUtils.joinToString(mappedSeq, "\n");
    }

    // Returns a random character from this char sequence.
    public static char random(final @NotNull CharSequence seq) {
        return random(seq, new Random());
    }

    // Returns a random character from this char sequence using the specified source of randomness.
    public static char random(final @NotNull CharSequence seq, final @NotNull Random random) {
        if (isEmpty(seq)) {
            throw new NoSuchElementException("Char sequence is empty.");
        }
        return seq.charAt(random.nextInt(seq.length()));
    }

    // Creates a new reader for the string.
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull StringReader reader(final @NotNull String str) {
        return new StringReader(str);
    }

    // Accumulates value starting with the first character and applying operation from left to right to current accumulator value and each character.
    public static char reduce(final @NotNull CharSequence seq, final @NotNull BiFunction<? super Character, ? super Character, Character> operation) {
        if (isEmpty(seq)) {
            throw new UnsupportedOperationException("Empty char sequence can't be reduced.");
        }
        char accumulator = seq.charAt(0);
        for (int i = 1; i <= lastIndex(seq); i++) {
            accumulator = operation.apply(accumulator, seq.charAt(i));
        }
        return accumulator;
    }

    // Accumulates value starting with the first character and applying operation from left to right to current accumulator value and each character with its index in the
    // original char sequence.
    public static char reduceIndexed(final @NotNull CharSequence seq,
                                     final @NotNull TriFunction<? super Integer, ? super Character, ? super Character, Character> operation) {
        if (isEmpty(seq)) {
            throw new UnsupportedOperationException("Empty char sequence can't be reduced.");
        }
        char accumulator = seq.charAt(0);
        for (int i = 1; i <= lastIndex(seq); i++) {
            accumulator = operation.apply(i, accumulator, seq.charAt(i));
        }
        return accumulator;
    }

    // Accumulates value starting with last character and applying operation from right to left to each character and current accumulator value.
    public static char reduceRight(final @NotNull CharSequence seq, final @NotNull BiFunction<? super Character, ? super Character, Character> operation) {
        int index = lastIndex(seq);
        if (index < 0) {
            throw new UnsupportedOperationException("Empty char sequence can't be reduced.");
        }
        char accumulator = seq.charAt(index--);
        while (index >= 0) {
            accumulator = operation.apply(seq.charAt(index--), accumulator);
        }
        return accumulator;
    }

    // Accumulates value starting with last character and applying operation from right to left to each character with its index in the original char sequence and current
    // accumulator value.
    public static char reduceRightIndexed(final @NotNull CharSequence seq,
                                          final @NotNull TriFunction<? super Integer, ? super Character, ? super Character, Character> operation) {
        int index = lastIndex(seq);
        if (index < 0) {
            throw new UnsupportedOperationException("Empty char sequence can't be reduced.");
        }
        char accumulator = seq.charAt(index--);
        while (index >= 0) {
            accumulator = operation.apply(index, seq.charAt(index), accumulator);
            --index;
        }
        return accumulator;
    }

    public static boolean regionMatches(final CharSequence first, final int firstOffset, final CharSequence second, final int secondOffset, final int length) {
        return regionMatches(first, firstOffset, second, secondOffset, length, false);
    }

    // Returns true if the specified range in first char sequence is equal to the specified range in second char sequence.
    public static boolean regionMatches(final CharSequence first, final int firstOffset, final CharSequence second, final int secondOffset, final int length,
                                        final boolean ignoreCase) {
        return first instanceof String && second instanceof String ?
            regionMatches((String) first, firstOffset, (String) second, secondOffset, length, ignoreCase) :
            regionMatchesImpl(first, firstOffset, second, secondOffset, length, ignoreCase);
    }

    @Contract(pure = true)
    public static boolean regionMatches(final String first, final int firstOffset, final String second,
                                        final int secondOffset, final int length) {
        return regionMatches(first, firstOffset, second, secondOffset, length, false);
    }

    // Returns true if the specified range in first string is equal to the specified range in second string.
    @Contract(pure = true)
    public static boolean regionMatches(final String first, final int firstOffset, final String second, final int secondOffset,
                                        final int length,
                                        final boolean ignoreCase) {
        return ignoreCase ? first.regionMatches(true, firstOffset, second, secondOffset, length) : first.regionMatches(firstOffset, second, secondOffset, length);
    }

    // If this char sequence starts with the given prefix, returns a new char sequence with the prefix removed. Otherwise, returns a new char sequence
    // with the same characters.
    public static @NotNull CharSequence removePrefix(final @NotNull CharSequence seq, final @NotNull CharSequence prefix) {
        return startsWith(seq, prefix) ? seq.subSequence(prefix.length(), seq.length()) : seq.subSequence(0, seq.length());
    }

    // If this string starts with the given prefix, returns a copy of this string with the prefix removed. Otherwise, returns this string.
    public static @NotNull String removePrefix(final @NotNull String str, final @NotNull CharSequence prefix) {
        return startsWith(str, prefix) ? str.substring(prefix.length()) : str;
    }

    // Returns a char sequence with content of this char sequence where its part at the given range is removed.
    public static @NotNull CharSequence removeRange(final @NotNull CharSequence seq, @NonNls final int startIndex, @NonNls final int endIndex) {
        if (endIndex < startIndex) {
            throw new IndexOutOfBoundsException("End index (" + endIndex + ") is less than start index (" + startIndex + ").");
        }
        final int length = seq.length();
        if (endIndex == startIndex) {
            return seq.subSequence(0, length);
        }
        final StringBuilder sb = new StringBuilder(length - (endIndex - startIndex));
        sb.append(seq, 0, startIndex);
        sb.append(seq, endIndex, length);
        return sb;
    }

    // Removes the part of a string at a given range.
    public static @NotNull String removeRange(final @NotNull String str, @NonNls final int startIndex, @NonNls final int endIndex) {
        return removeRange((CharSequence) str, startIndex, endIndex).toString();
    }

    // Returns a char sequence with content of this char sequence where its part at the given range is removed.
    // The end index of the range is included in the removed part.
    public static @NotNull CharSequence removeRange(final @NotNull CharSequence seq, final @NotNull IntRange range) {
        return removeRange(seq, range.getStart(), range.getEndInclusive() + 1);
    }

    // Removes the part of a string at the given range. The end index of the range is included in the removed part.
    public static @NotNull String removeRange(final @NotNull String seq, final @NotNull IntRange range) {
        return removeRange((CharSequence) seq, range).toString();
    }

    // If this char sequence ends with the given suffix, returns a new char sequence with the suffix removed. Otherwise, returns a new char sequence
    // with the same characters.
    public static @NotNull CharSequence removeSuffix(final @NotNull CharSequence seq, final @NotNull CharSequence suffix) {
        return endsWith(seq, suffix) ? seq.subSequence(0, seq.length() - suffix.length()) : seq.subSequence(0, seq.length());
    }

    // If this string ends with the given suffix, returns a copy of this string with the suffix removed. Otherwise, returns this string.
    public static @NotNull String removeSuffix(final @NotNull String str, final @NotNull CharSequence suffix) {
        return endsWith(str, suffix) ? str.substring(0, str.length() - suffix.length()) : str;
    }

    // When this char sequence starts with the given prefix and ends with the given suffix, returns a new char sequence having both the given prefix
    // and suffix removed. Otherwise returns a new char sequence with the same characters.
    public static @NotNull CharSequence removeSurrounding(final @NotNull CharSequence seq, final @NotNull CharSequence prefix,
                                                          final @NotNull CharSequence suffix) {
        final int length = seq.length();
        final int prefixLength = prefix.length();
        final int suffixLength = suffix.length();

        return length >= prefixLength + suffixLength && startsWith(seq, prefix) && endsWith(seq, suffix) ?
            seq.subSequence(prefixLength, length - suffixLength) :
            seq.subSequence(0, length);
    }

    // Removes from a string both the given prefix and suffix if and only if it starts with the prefix and ends with the suffix. Otherwise returns this string unchanged.
    public static @NotNull String removeSurrounding(final @NotNull String str, final @NotNull CharSequence prefix,
                                                    final @NotNull CharSequence suffix) {
        final int length = str.length();
        final int prefixLength = prefix.length();
        final int suffixLength = suffix.length();

        return length >= prefixLength + suffixLength && startsWith(str, prefix) && endsWith(str, suffix) ?
            str.substring(prefixLength, length - suffixLength) :
            str;
    }

    // When this char sequence starts with and ends with the given delimiter, returns a new char sequence having this delimiter removed both from the start and end.
    // Otherwise returns a new char sequence with the same characters.
    public static @NotNull CharSequence removeSurrounding(final @NotNull CharSequence seq, final @NotNull CharSequence delimiter) {
        return removeSurrounding(seq, delimiter, delimiter);
    }

    // Removes the given delimiter string from both the start and the end of this string if and only if it starts with and ends with the delimiter. Otherwise returns
    // this string unchanged.
    public static @NotNull String removeSurrounding(final @NotNull String str, final @NotNull CharSequence delimiter) {
        return removeSurrounding(str, delimiter, delimiter);
    }

    public static @NotNull String replace(final @NotNull String str, final char oldChar, final char newChar) {
        return replace(str, oldChar, newChar, false);
    }

    // Returns a new string with all occurrences of oldChar replaced with newChar.
    public static @NotNull String replace(final @NotNull String str, final char oldChar, final char newChar, final boolean ignoreCase) {
        if (ignoreCase) {
            final Sequence<String> sequence = splitToSequence(str, new char[]{oldChar}, true);
            return SequenceUtils.joinToString(sequence, Character.toString(newChar));
        } else {
            return str.replace(oldChar, newChar);
        }
    }

    public static @NotNull String replace(final @NotNull String str, final @NotNull String oldValue, final @NotNull String newValue) {
        return replace(str, oldValue, newValue, false);
    }

    // Returns a new string obtained by replacing all occurrences of the oldValue substring in this string with the specified newValue string.
    public static @NotNull String replace(final @NotNull String str, final @NotNull String oldValue, final @NotNull String newValue, final boolean ignoreCase) {
        final Sequence<String> sequence = splitToSequence(str, singletonList(oldValue), ignoreCase);
        return SequenceUtils.joinToString(sequence, newValue);
    }

    public static @NotNull String replaceAfter(final @NotNull String str, final char delimiter, final @NotNull String replacement) {
        return replaceAfter(str, delimiter, replacement, str);
    }

    // Replace part of string after the first occurrence of given delimiter with the replacement string. If the string does not contain the delimiter,
    // returns missingDelimiterValue.
    public static @NotNull String replaceAfter(final @NotNull String str, final char delimiter, final @NotNull String replacement,
                                               final @NotNull String missingDelimiterValue) {
        final int index = indexOf(str, delimiter);
        return index == -1 ? missingDelimiterValue : replaceRange(str, index + 1, str.length(), replacement);
    }

    public static @NotNull String replaceAfter(final @NotNull String str, final @NotNull String delimiter, final @NotNull String replacement) {
        return replaceAfter(str, delimiter, replacement, str);
    }

    public static @NotNull String replaceAfter(final @NotNull String str, final @NotNull String delimiter, final @NotNull String replacement,
                                               final @NotNull String missingDelimiterValue) {
        final int index = indexOf(str, delimiter);
        return index == -1 ? missingDelimiterValue : replaceRange(str, index + delimiter.length(), str.length(), replacement);
    }

    public static @NotNull String replaceAfterLast(final @NotNull String str, final @NotNull String delimiter, final @NotNull String replacement) {
        return replaceAfterLast(str, delimiter, replacement, str);
    }

    public static @NotNull String replaceAfterLast(final @NotNull String str, final @NotNull String delimiter, final @NotNull String replacement,
                                                   final @NotNull String missingDelimiterValue) {
        final int index = lastIndexOf(str, delimiter);
        return index == -1 ? missingDelimiterValue : replaceRange(str, index + delimiter.length(), str.length(), replacement);
    }

    public static @NotNull String replaceAfterLast(final @NotNull String str, final char delimiter, final @NotNull String replacement) {
        return replaceAfterLast(str, delimiter, replacement, str);
    }

    public static @NotNull String replaceAfterLast(final @NotNull String str, final char delimiter, final @NotNull String replacement,
                                                   final @NotNull String missingDelimiterValue) {
        final int index = lastIndexOf(str, delimiter);
        return index == -1 ? missingDelimiterValue : replaceRange(str, index + 1, str.length(), replacement);
    }

    public static @NotNull String replaceBefore(final @NotNull String str, final char delimiter, final @NotNull String replacement) {
        return replaceBefore(str, delimiter, replacement, str);
    }

    public static @NotNull String replaceBefore(final @NotNull String str, final char delimiter, final @NotNull String replacement,
                                                final @NotNull String missingDelimiterValue) {
        final int index = indexOf(str, delimiter);
        return index == -1 ? missingDelimiterValue : replaceRange(str, 0, index, replacement);
    }

    public static @NotNull String replaceBefore(final @NotNull String str, final @NotNull String delimiter, final @NotNull String replacement) {
        return replaceBefore(str, delimiter, replacement, str);
    }

    public static @NotNull String replaceBefore(final @NotNull String str, final @NotNull String delimiter, final @NotNull String replacement,
                                                final @NotNull String missingDelimiterValue) {
        final int index = indexOf(str, delimiter);
        return index == -1 ? missingDelimiterValue : replaceRange(str, 0, index, replacement);
    }

    public static @NotNull String replaceBeforeLast(final @NotNull String str, final char delimiter, final @NotNull String replacement) {
        return replaceBeforeLast(str, delimiter, replacement, str);
    }

    public static @NotNull String replaceBeforeLast(final @NotNull String str, final char delimiter, final @NotNull String replacement,
                                                    final @NotNull String missingDelimiterValue) {
        final int index = lastIndexOf(str, delimiter);
        return index == -1 ? missingDelimiterValue : replaceRange(str, 0, index, replacement);
    }

    public static @NotNull String replaceBeforeLast(final @NotNull String str, final String delimiter, final @NotNull String replacement) {
        return replaceBeforeLast(str, delimiter, replacement, str);
    }

    public static @NotNull String replaceBeforeLast(final @NotNull String str, final String delimiter, final @NotNull String replacement,
                                                    final @NotNull String missingDelimiterValue) {
        final int index = lastIndexOf(str, delimiter);
        return index == -1 ? missingDelimiterValue : replaceRange(str, 0, index, replacement);
    }

    public static @NotNull String replaceFirst(final @NotNull String str, final char oldChar, final char newChar) {
        return replaceFirst(str, oldChar, newChar, false);
    }

    // Returns a new string with the first occurrence of oldChar replaced with newChar.
    public static @NotNull String replaceFirst(final @NotNull String str, final char oldChar, final char newChar, final boolean ignoreCase) {
        final int index = indexOf(str, oldChar, ignoreCase);
        return index < 0 ? str : replaceRange(str, index, index + 1, Character.toString(newChar));
    }

    public static @NotNull String replaceFirst(final @NotNull String str, final @NotNull String oldValue, final @NotNull String newValue) {
        return replaceFirst(str, oldValue, newValue, false);
    }

    public static @NotNull String replaceFirst(final @NotNull String str, final String oldValue, final String newValue, final boolean ignoreCase) {
        final int index = indexOf(str, oldValue, ignoreCase);
        return index < 0 ? str : replaceRange(str, index, index + oldValue.length(), newValue);
    }

    public static @NotNull String replaceIndent(final @NotNull String str, final @NotNull String newIndent) {
        final List<String> lines = lines(str);
        final List<String> filter = CollectionUtils.filter(lines, StringUtils::isNotBlank);
        final List<Integer> map = CollectionUtils.map(filter, StringUtils::indentWidth);
        final int minCommonIndent = CollectionUtils.min(map).orElse(0);

        return CollectionUtils.reindent(lines, str.length() + newIndent.length() * lines.size(), getIndentFunction(newIndent), line -> drop(line, minCommonIndent));
    }

    public static @NotNull String replaceIndentByMargin(final @NotNull String str) {
        return replaceIndentByMargin(str, "", "|");
    }

    public static @NotNull String replaceIndentByMargin(final @NotNull String str, final @NotNull String newIndent) {
        return replaceIndentByMargin(str, newIndent, "|");
    }

    // Detects indent by marginPrefix as it does trimMargin and replace it with newIndent.
    public static @NotNull String replaceIndentByMargin(final @NotNull String str, final @NotNull String newIndent, final @NotNull String marginPrefix) {
        if (isNotBlank(marginPrefix)) {
            throw new IllegalArgumentException("marginPrefix must be non-blank string.");
        }
        final List<String> lines = lines(str);
        return CollectionUtils.reindent(lines, str.length() + newIndent.length() * lines.size(), getIndentFunction(newIndent), line -> {
            final int firstNonWhitespaceIndex = indexOfFirst(line, c -> !isWhitespace(c));

            if (firstNonWhitespaceIndex == -1) {
                return null;
            } else if (startsWith(line, marginPrefix, firstNonWhitespaceIndex)) {
                return substring(line, firstNonWhitespaceIndex + marginPrefix.length());
            } else {
                return null;
            }
        });
    }

    // Returns a char sequence with content of this char sequence where its part at the given range is replaced with the replacement char sequence.
    public static @NotNull CharSequence replaceRange(final @NotNull CharSequence seq, @NonNls final int startIndex, @NonNls final int endIndex,
                                                     final @NotNull CharSequence replacement) {
        if (endIndex < startIndex) {
            throw new IndexOutOfBoundsException("End index (" + endIndex + ") is less than start index (" + startIndex + ").");
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(seq, 0, startIndex);
        sb.append(replacement);
        sb.append(seq, endIndex, seq.length());
        return sb;
    }

    public static @NotNull String replaceRange(final @NotNull String str, final int startIndex, final int endIndex, final @NotNull CharSequence replacement) {
        return replaceRange((CharSequence) str, startIndex, endIndex, replacement).toString();
    }

    public static @NotNull CharSequence replaceRange(final @NotNull CharSequence seq, final @NotNull IntRange range, final @NotNull CharSequence replacement) {
        return replaceRange(seq, range.getStart(), range.getEndInclusive() + 1, replacement);
    }

    public static @NotNull String replaceRange(final @NotNull String seq, final @NotNull IntRange range, final @NotNull CharSequence replacement) {
        return replaceRange((CharSequence) seq, range, replacement).toString();
    }

    // Returns a char sequence with characters in reversed order.
    public static @NotNull CharSequence reversed(final @NotNull CharSequence seq) {
        return new StringBuilder(seq).reverse();
    }

    // Returns a string with characters in reversed order.
    public static @NotNull String reversed(final String str) {
        return reversed((CharSequence) str).toString();
    }

    // Returns the single character, or throws an exception if the char sequence is empty or has more than one character.
    public static char single(final @NotNull CharSequence seq) {
        switch (seq.length()) {
            case 0:
                throw new NoSuchElementException("Char sequence is empty.");
            case 1:
                return seq.charAt(0);
            default:
                throw new IllegalArgumentException("Char sequence has more than one element.");
        }
    }

    // Returns the single character matching the given predicate, or throws exception if there is no or more than one matching character.
    public static char single(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        Character single = null;
        boolean found = false;
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                if (found) {
                    throw new IllegalArgumentException("Char sequence contains more than one matching element.");
                }
                single = element;
                found = true;
            }
        }
        if (!found) {
            throw new NoSuchElementException("Char sequence contains no character matching the predicate.");
        }
        return single;
    }

    // Returns single character, or null if the char sequence is empty or has more than one character.
    public static @Nullable Character singleOrNull(final @NotNull CharSequence seq) {
        return seq.length() == 1 ? seq.charAt(0) : null;
    }

    // Returns the single character matching the given predicate, or null if character was not found or more than one character was found.
    public static @Nullable Character singleOrNull(final @NotNull CharSequence seq, final @NotNull Predicate<? super Character> predicate) {
        Character single = null;
        boolean found = false;
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                if (found) {
                    return null;
                }
                single = element;
                found = true;
            }
        }
        if (!found) {
            return null;
        }
        return single;
    }

    // Returns a char sequence containing characters of the original char sequence at the specified range of indices.
    public static @NotNull CharSequence slice(final @NotNull CharSequence seq, final @NotNull IntRange indices) {
        return indices.isEmpty() ? "" : subSequence(seq, indices);
    }

    // Returns a string containing characters of the original string at the specified range of indices.
    public static @NotNull String slice(final @NotNull String str, final @NotNull IntRange indices) {
        return indices.isEmpty() ? "" : substring(str, indices);
    }

    // Returns a char sequence containing characters of the original char sequence at specified indices.
    public static @NotNull CharSequence slice(final @NotNull CharSequence seq, final @NotNull Iterable<Integer> indices) {
        final int size = CollectionUtils.collectionSizeOrDefault(indices, 10);
        if (size == 0) {
            return "";
        }
        final StringBuilder result = new StringBuilder(size);
        for (final int i : indices) {
            result.append(seq.charAt(i));
        }
        return result;
    }

    // Returns a string containing characters of the original string at specified indices.
    public static @NotNull String slice(final @NotNull String str, final @NotNull Iterable<Integer> indices) {
        return slice((CharSequence) str, indices).toString();
    }

    public static @NotNull List<String> split(final @NotNull CharSequence seq, final @NotNull Collection<String> delimiters) {
        return split(seq, delimiters, false, 0);
    }

    public static @NotNull List<String> split(final @NotNull CharSequence seq, final @NotNull Collection<String> delimiters, final boolean ignoreCase) {
        return split(seq, delimiters, ignoreCase, 0);
    }

    public static @NotNull List<String> split(final @NotNull CharSequence seq, final @NotNull Collection<String> delimiters,
                                              final @Range(from = 0, to = Integer.MAX_VALUE) int limit) {
        return split(seq, delimiters, false, limit);
    }

    // Splits this char sequence to a list of strings around occurrences of the specified delimiters.
    public static @NotNull List<String> split(final @NotNull CharSequence seq, final @NotNull Collection<String> delimiters, final boolean ignoreCase,
                                              final @Range(from = 0, to = Integer.MAX_VALUE) int limit) {
        if (delimiters.size() == 1) {
            final String delimiter = delimiters.iterator().next();
            if (!isEmpty(delimiter)) {
                return split(seq, delimiter, ignoreCase, limit);
            }
        }
        final Sequence<IntRange> sequence = rangesDelimitedBy(seq, delimiters, ignoreCase, limit);
        final Iterable<IntRange> iterable = SequenceUtils.asIterable(sequence);
        return CollectionUtils.map(iterable, range -> substring(seq, range));
    }

    public static @NotNull List<String> split(final @NotNull CharSequence seq, final @NotNull char[] delimiters) {
        return split(seq, delimiters, false, 0);
    }

    public static @NotNull List<String> split(final @NotNull CharSequence seq, final @NotNull char[] delimiters, final boolean ignoreCase) {
        return split(seq, delimiters, ignoreCase, 0);
    }

    public static @NotNull List<String> split(final @NotNull CharSequence seq, final @NotNull char[] delimiters,
                                              final @Range(from = 0, to = Integer.MAX_VALUE) int limit) {
        return split(seq, delimiters, false, limit);
    }

    // Splits this char sequence to a list of strings around occurrences of the specified delimiters.
    public static @NotNull List<String> split(final @NotNull CharSequence seq, final @NotNull char[] delimiters, final boolean ignoreCase,
                                              final @Range(from = 0, to = Integer.MAX_VALUE) int limit) {
        if (delimiters.length == 1) {
            return split(seq, Character.toString(delimiters[0]), ignoreCase, limit);
        }
        final Sequence<IntRange> sequence = rangesDelimitedBy(seq, delimiters, ignoreCase, limit);
        final Iterable<IntRange> iterable = SequenceUtils.asIterable(sequence);
        return CollectionUtils.map(iterable, range -> substring(seq, range));
    }

    //////

    public static CharSequence subSequence(final CharSequence seq, final IntRange range) {
        return seq.subSequence(range.getStart(), range.getEndInclusive() + 1);
    }

    public static boolean startsWith(final CharSequence seq, final CharSequence prefix, final int startIndex) {
        return startsWith(seq, prefix, startIndex, false);
    }

    public static boolean startsWith(final CharSequence seq, final CharSequence prefix, final int startIndex, final boolean ignoreCase) {
        return !ignoreCase && seq instanceof String && prefix instanceof String ?
            ((String) seq).startsWith((String) prefix, startIndex) :
            regionMatchesImpl(seq, startIndex, prefix, 0, prefix.length(), ignoreCase);
    }

    public static boolean startsWith(final CharSequence seq, final CharSequence prefix) {
        return startsWith(seq, prefix, false);
    }

    public static boolean startsWith(final CharSequence seq, final CharSequence prefix, final boolean ignoreCase) {
        return !ignoreCase && seq instanceof String && prefix instanceof String ?
            ((String) seq).startsWith((String) prefix) :
            regionMatchesImpl(seq, 0, prefix, 0, prefix.length(), ignoreCase);
    }

    public static @NotNull Sequence<String> splitToSequence(final @NotNull CharSequence seq, final @NotNull Collection<String> delimiters) {
        return splitToSequence(seq, delimiters, false, 0);
    }

    public static @NotNull Sequence<String> splitToSequence(final @NotNull CharSequence seq, final @NotNull Collection<String> delimiters,
                                                            final boolean ignoreCase) {
        return splitToSequence(seq, delimiters, ignoreCase, 0);
    }

    public static @NotNull Sequence<String> splitToSequence(final @NotNull CharSequence seq, final @NotNull Collection<String> delimiters,
                                                            final int limit) {
        return splitToSequence(seq, delimiters, false, limit);
    }

    public static @NotNull Sequence<String> splitToSequence(final @NotNull CharSequence seq, final @NotNull Collection<String> delimiters,
                                                            final boolean ignoreCase, final int limit) {
        return SequenceUtils.map(rangesDelimitedBy(seq, delimiters), range -> substring(seq, range));
    }

    public static Sequence<String> splitToSequence(final CharSequence seq, final char[] delimiters, final boolean ignoreCase) {
        return SequenceUtils.map(rangesDelimitedBy(seq, delimiters, ignoreCase, 0), range -> substring(seq, range));
    }

    public static String substring(final CharSequence seq, final IntRange range) {
        return seq.subSequence(range.getStart(), range.getEndInclusive() + 1).toString();
    }

    public static String substring(final String str, final IntRange range) {
        return substring(str, range.getStart(), range.getEndInclusive() + 1);
    }

    public static String substring(final CharSequence seq, final int startIndex) {
        return substring(seq, startIndex, seq.length());
    }

    public static String substring(final CharSequence seq, final int startIndex, final int endIndex) {
        return seq.subSequence(startIndex, endIndex).toString();
    }

    public static CharSequence take(final CharSequence seq, @NonNls final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Requested character count " + n + " is less than zero.");
        }
        return seq.subSequence(0, coerceAtMost(n, seq.length()));
    }

    public static String take(final String str, @NonNls final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Requested character count " + n + " is less than zero.");
        }
        return str.substring(0, coerceAtMost(n, str.length()));
    }

    @Contract(pure = true)
    public static @NotNull byte[] toByteArray(final @NotNull String str) {
        return toByteArray(str, UTF_8);
    }

    @Contract(pure = true)
    public static @NotNull byte[] toByteArray(final @NotNull String str, final @NotNull Charset charset) {
        return str.getBytes(charset);
    }

    public static List<String> windowed(final CharSequence seq, final int size) {
        return windowed(seq, size, 1, false, CharSequence::toString);
    }

    public static List<String> windowed(final CharSequence seq, final int size, final int step) {
        return windowed(seq, size, step, false, CharSequence::toString);
    }

    public static List<String> windowed(final CharSequence seq, final int size, final boolean partialWindows) {
        return windowed(seq, size, 1, partialWindows, CharSequence::toString);
    }

    public static List<String> windowed(final CharSequence seq, final int size, final int step,
                                        final boolean partialWindows) {
        return windowed(seq, size, step, partialWindows, CharSequence::toString);
    }

    public static <R> List<R> windowed(final @NotNull CharSequence seq, final int size, final int step,
                                       final boolean partialWindows,
                                       final @NotNull Function<? super CharSequence, ? extends R> transform) {
        final int length = seq.length();
        final List<R> result = new ArrayList<>(length + step - 1 / step);
        int index = 0;
        while (index < length) {
            final int end = index + size;
            final int coercedEnd;
            if (end > length) {
                if (partialWindows) {
                    coercedEnd = length;
                } else {
                    break;
                }
            } else {
                coercedEnd = end;
            }
            result.add(transform.apply(seq.subSequence(index, coercedEnd)));
            index += step;
        }
        return result;
    }

    // Private Stuff

    // Implementation of regionMatches for CharSequences.
    private static boolean regionMatchesImpl(final CharSequence first, final int firstOffset, final CharSequence second, final int secondOffset,
                                             final int length) {
        return regionMatchesImpl(first, firstOffset, second, secondOffset, length, false);
    }

    // Implementation of regionMatches for CharSequences ignoring the case
    private static boolean regionMatchesImpl(final CharSequence first, final int firstOffset, final CharSequence second,
                                             final int secondOffset, final int length, final boolean ignoreCase) {
        final int firstLength = first.length();
        final int secondLength = second.length();

        if (secondOffset < 0 || firstOffset < 0 || firstOffset > firstLength - length || secondOffset > secondLength - length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (!CharUtils.equals(first.charAt(firstOffset + i), second.charAt(secondOffset + i), ignoreCase)) {
                return false;
            }
        }
        return true;
    }

    @Contract("_, _, _, _ -> param2")
    private static @NotNull <C extends Appendable> C filterTo(final @NotNull CharSequence seq, final @NotNull C destination, final int length,
                                                              final @NotNull Predicate<? super Character> predicate) {
        for (int i = 0; i < length; i++) {
            final char element = seq.charAt(i);
            if (predicate.test(element)) {
                try {
                    destination.append(element);
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        return destination;
    }

    private static Optional<Pair<Integer, String>> findAnyOf(final CharSequence seq, final Collection<String> strings, final int startIndex,
                                                             final boolean ignoreCase, final boolean last) {

        if (!ignoreCase && strings.size() == 1) {
            final String str = CollectionUtils.single(strings);
            final int index = last ? str.lastIndexOf(startIndex) : str.indexOf(startIndex);
            return index < 0 ? empty() : of(new Pair<>(index, str));
        }

        if (seq instanceof String) {
            if (last) {
                final int start = coerceAtMost(startIndex, lastIndex(seq));
                final int end = 0;
                for (int i = start; i >= end; i--) {
                    final int index = i;
                    final String matchingString = CollectionUtils.firstOrNull(strings,
                        it -> regionMatches(it, 0, (String) seq, index, it.length(), ignoreCase));
                    if (matchingString != null) {
                        return of(new Pair<>(index, matchingString));
                    }
                }
            } else {
                final int start = coerceAtLeast(startIndex, 0);
                final int end = seq.length();
                for (int i = start; i <= end; i++) {
                    final int index = i;
                    final String matchingString = CollectionUtils.firstOrNull(strings,
                        it -> regionMatches(it, 0, (String) seq, index, it.length(), ignoreCase));
                    if (matchingString != null) {
                        return of(new Pair<>(index, matchingString));
                    }
                }
            }
        } else {
            if (last) {
                final int start = coerceAtMost(startIndex, lastIndex(seq));
                final int end = 0;
                for (int i = start; i >= end; i--) {
                    final int index = i;
                    final String matchingString = CollectionUtils.firstOrNull(strings,
                        it -> regionMatchesImpl(it, 0, seq, index, it.length(), ignoreCase));
                    if (matchingString != null) {
                        return of(new Pair<>(index, matchingString));
                    }
                }
            } else {
                final int start = coerceAtLeast(startIndex, 0);
                final int end = seq.length();
                for (int i = start; i <= end; i++) {
                    final int index = i;
                    final String matchingString = CollectionUtils.firstOrNull(strings,
                        it -> regionMatchesImpl(it, 0, seq, index, it.length(), ignoreCase));
                    if (matchingString != null) {
                        return of(new Pair<>(index, matchingString));
                    }
                }
            }
        }

        return empty();
    }

    private static int indexOf(final CharSequence first, final CharSequence second, final int startIndex, final int endIndex,
                               final boolean ignoreCase, final boolean last) {
        if (first instanceof String && second instanceof String) {
            if (last) {
                final int start = coerceAtMost(startIndex, lastIndex(first));
                final int end = coerceAtLeast(endIndex, 0);
                for (int i = start; i >= end; i--) {
                    if (regionMatches((String) second, 0, (String) first, i, second.length(), ignoreCase)) {
                        return i;
                    }
                }
            } else {
                final int start = coerceAtLeast(startIndex, 0);
                final int end = coerceAtMost(endIndex, first.length());
                for (int i = start; i <= end; i++) {
                    if (regionMatches((String) second, 0, (String) first, i, second.length(), ignoreCase)) {
                        return i;
                    }
                }
            }
        } else {
            if (last) {
                final int start = coerceAtMost(startIndex, lastIndex(first));
                final int end = coerceAtLeast(endIndex, 0);
                for (int i = start; i >= end; i--) {
                    if (regionMatchesImpl(second, 0, first, i, second.length(), ignoreCase)) {
                        return i;
                    }
                }
            } else {
                final int start = coerceAtLeast(startIndex, 0);
                final int end = coerceAtMost(endIndex, first.length());
                for (int i = start; i <= end; i++) {
                    if (regionMatchesImpl(second, 0, first, i, second.length(), ignoreCase)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    @Contract(value = "_, _ -> new", pure = true)
    private static @NotNull Sequence<IntRange> rangesDelimitedBy(final @NotNull CharSequence seq, final @NotNull Collection<String> delimiters) {
        return rangesDelimitedBy(seq, delimiters, 0, false, 0);
    }

    @Contract(pure = true)
    private static @NotNull Sequence<IntRange> rangesDelimitedBy(final @NotNull CharSequence seq, final @NotNull Collection<String> delimiters,
                                                                 final boolean ignoreCase, final int limit) {
        return rangesDelimitedBy(seq, delimiters, 0, ignoreCase, limit);
    }

    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    private static @NotNull Sequence<IntRange> rangesDelimitedBy(final @NotNull CharSequence seq, final @NotNull Collection<String> delimiters,
                                                                 final int startIndex, final boolean ignoreCase, final int limit) {
        return new DelimitedRangesSequence(seq, startIndex, limit, (charSeq, currentIndex) -> {
            final Optional<Pair<Integer, String>> optional = findAnyOf(charSeq, delimiters, currentIndex, ignoreCase, false);
            if (optional.isPresent()) {
                final Pair<Integer, String> pair = optional.get();
                return of(new Pair<>(pair.getFirst(), pair.getSecond().length()));
            } else {
                return empty();
            }
        });
    }

    @Contract(pure = true)
    private static @NotNull Sequence<IntRange> rangesDelimitedBy(final CharSequence seq, final char[] delimiters, final boolean ignoreCase, final int limit) {
        return rangesDelimitedBy(seq, delimiters, 0, ignoreCase, limit);
    }

    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    private static @NotNull Sequence<IntRange> rangesDelimitedBy(final CharSequence seq, final char[] delimiters, final int startIndex,
                                                                 final boolean ignoreCase, final int limit) {
        return new DelimitedRangesSequence(seq, startIndex, limit, (charSeq, currentIndex) -> {
            final int i = indexOfAny(charSeq, delimiters, currentIndex, ignoreCase);
            return i < 0 ? empty() : of(new Pair<>(i, 1));
        });
    }

    private static int indentWidth(final String str) {
        final int index = indexOfFirst(str, c -> !isWhitespace(c));
        return index == -1 ? str.length() : index;
    }

    @NonNls
    private static @NotNull Function<? super String, String> getIndentFunction(final String indent) {
        return isEmpty(indent) ? (line -> line) : (line -> indent + line);
    }

    private static @NotNull List<String> split(final @NotNull CharSequence seq, final @NotNull String delimiter, final boolean ignoreCase,
                                               final @Range(from = 0, to = Integer.MAX_VALUE) int limit) {
        int currentOffset = 0;
        int nextIndex = indexOf(seq, delimiter, currentOffset, ignoreCase);
        if (nextIndex == -1 || limit == 1) {
            return singletonList(seq.toString());
        }
        final boolean isLimited = limit > 0;
        final int size = isLimited ? coerceAtMost(limit, 10) : 10;
        final List<String> result = new ArrayList<>(size);
        do {
            result.add(substring(seq, currentOffset, nextIndex));
            currentOffset = nextIndex + delimiter.length();
            // Do not search for next occurrence if we're reaching limit
            if (isLimited && result.size() == limit - 1) {
                break;
            }
            nextIndex = indexOf(seq, delimiter, currentOffset, ignoreCase);
        } while (nextIndex != -1);
        result.add(substring(seq, currentOffset, seq.length()));
        return result;
    }
}

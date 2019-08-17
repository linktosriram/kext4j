package io.github.linktosriram.kext4j.sequence;

import io.github.linktosriram.kext4j.Pair;
import io.github.linktosriram.kext4j.range.IntRange;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;

import static io.github.linktosriram.kext4j.range.RangeUtils.coerceIn;
import static io.github.linktosriram.kext4j.text.StringUtils.lastIndex;

public class DelimitedRangesSequence implements Sequence<IntRange> {

    private final CharSequence input;
    private final int startIndex;
    private final int limit;
    private final BiFunction<? super CharSequence, ? super Integer, Optional<Pair<Integer, Integer>>> getNextMatch;

    @Contract(pure = true)
    public DelimitedRangesSequence(final CharSequence input, final int startIndex, final int limit,
                                   final BiFunction<? super CharSequence, ? super Integer, Optional<Pair<Integer, Integer>>> getNextMatch) {
        this.input = input;
        this.startIndex = startIndex;
        this.limit = limit;
        this.getNextMatch = getNextMatch;
    }

    @Override
    public @NotNull Iterator<IntRange> iterator() {
        return new Iterator<IntRange>() {
            private int nextState = -1; // -1 for unknown, 0 for done, 1 for continue
            private int currentStartIndex = coerceIn(startIndex, 0, input.length());
            private int nextSearchIndex = currentStartIndex;
            private @Nullable IntRange nextItem;
            private int counter;

            private void calcNext() {
                if (nextSearchIndex < 0) {
                    nextState = 0;
                    nextItem = null;
                } else {
                    if (limit > 0 && ++counter >= limit || nextSearchIndex > input.length()) {
                        nextItem = new IntRange(currentStartIndex, lastIndex(input));
                        nextSearchIndex = -1;
                    } else {
                        final Optional<Pair<Integer, Integer>> match = getNextMatch.apply(input, nextSearchIndex);
                        if (match.isPresent()) {
                            final Pair<Integer, Integer> pair = match.get();
                            final int index = pair.getFirst();
                            final int length = pair.getSecond();
                            nextItem = new IntRange(currentStartIndex, index - 1);
                            currentStartIndex = index + length;
                            nextSearchIndex = currentStartIndex + (length == 0 ? 1 : 0);
                        } else {
                            nextItem = new IntRange(currentStartIndex, lastIndex(input));
                            nextSearchIndex = -1;
                        }
                    }
                    nextState = 1;
                }
            }

            @Override
            public IntRange next() {
                if (nextState == -1) {
                    calcNext();
                }
                if (nextState == 0) {
                    throw new NoSuchElementException();
                }
                final IntRange result = nextItem;
                // Clean next to avoid keeping reference on yielded instance
                nextItem = null;
                nextState = -1;
                return result;
            }

            @Override
            public boolean hasNext() {
                if (nextState == -1) {
                    calcNext();
                }
                return nextState == 1;
            }
        };
    }
}

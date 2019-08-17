package io.github.linktosriram.kext4j.sequence;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;

class TransformingSequence<T, R> implements Sequence<R> {

    private final Sequence<T> sequence;
    private final Function<? super T, ? extends R> transformer;

    @Contract(pure = true)
    TransformingSequence(final Sequence<T> sequence, final Function<? super T, ? extends R> transformer) {
        this.sequence = sequence;
        this.transformer = transformer;
    }

    @Override
    public @NotNull Iterator<R> iterator() {
        return new Iterator<R>() {
            private final Iterator<T> iterator = sequence.iterator();

            @Override
            public R next() {
                return transformer.apply(iterator.next());
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
        };
    }
}

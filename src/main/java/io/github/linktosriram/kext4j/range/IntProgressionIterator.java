package io.github.linktosriram.kext4j.range;

import io.github.linktosriram.kext4j.collection.IntIterator;

import java.util.NoSuchElementException;

// An iterator over a progression of values of type int`.
class IntProgressionIterator extends IntIterator {
    private final int finalElement;
    private final int step;

    private boolean hasNext;
    private int next;

    IntProgressionIterator(final int first, final int last, final int step) {
        this.step = step;

        finalElement = last;
        hasNext = step > 0 ? first <= last : first >= last;
        next = hasNext ? first : finalElement;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public int nextInt() {
        final int value = next;
        if (value == finalElement) {
            if (!hasNext) {
                throw new NoSuchElementException();
            }
            hasNext = false;
        } else {
            next += step;
        }
        return value;
    }
}

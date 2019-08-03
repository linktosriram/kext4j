package io.github.linktosriram.kext4j.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * An iterator over a sequence of values of type {@link Character}.
 */
public abstract class CharIterator implements Iterator<Character> {

    @Override
    public @NotNull Character next() {
        return nextChar();
    }

    // Returns the next value in the sequence without boxing
    public abstract char nextChar();
}

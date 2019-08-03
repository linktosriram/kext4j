package io.github.linktosriram.kext4j.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public abstract class IntIterator implements Iterator<Integer> {

    @Override
    public @NotNull Integer next() {
        return nextInt();
    }

    public abstract int nextInt();
}

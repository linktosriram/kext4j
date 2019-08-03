package io.github.linktosriram.kext4j;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public final class Pair<A, B> {

    private final A first;
    private final B second;

    @Contract(pure = true)
    public Pair(final A first, final B second) {
        this.first = first;
        this.second = second;
    }

    @Contract(pure = true)
    public A getFirst() {
        return first;
    }

    @Contract(pure = true)
    public B getSecond() {
        return second;
    }

    @NonNls
    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "(" + first + ", " + second + ")";
    }
}

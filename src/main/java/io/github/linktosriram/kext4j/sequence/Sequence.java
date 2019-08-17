package io.github.linktosriram.kext4j.sequence;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@FunctionalInterface
public interface Sequence<T> {
    @NotNull Iterator<T> iterator();
}

package io.github.linktosriram.kext4j.sequence;

import java.util.Iterator;

@FunctionalInterface
public interface Sequence<T> {
    Iterator<T> iterator();
}

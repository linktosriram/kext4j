package io.github.linktosriram.kext4j.function;

@FunctionalInterface
public interface TriFunction<T, U, R, V> {
    V apply(T t, U u, R r);
}

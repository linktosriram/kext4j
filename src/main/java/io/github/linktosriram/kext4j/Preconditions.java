package io.github.linktosriram.kext4j;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class Preconditions {

    @Contract(value = " -> fail", pure = true)
    private Preconditions() {
        throw new AssertionError();
    }

    public static void require(final boolean value, final @NotNull Supplier<Object> lazyMessage) {
        if (!value) {
            final Object message = lazyMessage.get();
            throw new IllegalArgumentException(message.toString());
        }
    }
}

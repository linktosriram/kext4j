package io.github.linktosriram.kext4j.text.regex;

import org.jetbrains.annotations.Contract;

import java.util.regex.Pattern;

public class Regex {

    private final Pattern nativePattern;

    @Contract(pure = true)
    private Regex(final Pattern nativePattern) {
        this.nativePattern = nativePattern;
    }

    public Regex(final String pattern) {
        this(Pattern.compile(pattern));
    }

    public boolean matches(final CharSequence input) {
        return nativePattern.matcher(input).matches();
    }
}

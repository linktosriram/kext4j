# kext4j

Kotlin Extension Functions for Java.

Implementation of kotlin extension methods for Java.

Currently in-progress:
* https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html

## Provided functions

### StringUtils

* [indices](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/indices.html)
* [lastIndex](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/last-index.html)
* [all](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/all.html)
* [any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/any.html)
* [asIterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/as-iterable.html)
* [asSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/as-sequence.html)
* [byteInputStream](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/byte-input-stream.html)
* [capitalize](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/capitalize.html)
* [chunked](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/chunked.html)
* [commonPrefixWith](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/common-prefix-with.html)
* [commonSuffixWith](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/common-suffix-with.html)
* [contains](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/contains.html)
* [count](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/count.html)
* [decapitalize](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/decapitalize.html)
* [drop](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/drop.html)
* [dropLast](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/drop-last.html)
* [dropLastWhile](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/drop-last-while.html)
* [dropWhile](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/drop-while.html)
* [elementAtOrElse](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/element-at-or-else.html)
* [elementAtOrNull](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/element-at-or-null.html)
* [endsWith](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/ends-with.html)
* [filter](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/filter.html)
* [filterIndexed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/filter-indexed.html)
* [filterIndexedTo](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/filter-indexed-to.html)
* [filterNot](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/filter-not.html)
* [filterNotTo](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/filter-not-to.html)
* [filterTo](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/filter-to.html)
* [find](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/find.html)
* [findAnyOf](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/find-any-of.html)
* [findLast](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/find-last.html)
* [findLastAnyOf](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/find-last-any-of.html)
* [first](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/first.html)
* [firstOrNull](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/first-or-null.html)
* [flatMap](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/flat-map.html)
* [flatMapTo](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/flat-map-to.html)
* [fold](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/fold.html)
* [foldIndexed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/fold-indexed.html)
* [foldRight](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/fold-right.html)
* [foldRightIndexed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/fold-right-indexed.html)
* [forEach](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/for-each.html)
* [forEachIndexed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/for-each-indexed.html)
* [getOrElse](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/get-or-else.html)
* [getOrNull](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/get-or-null.html)
* [groupBy](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/group-by.html)
* [groupByTo](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/group-by-to.html)
* [hasSurrogatePairAt](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/has-surrogate-pair-at.html)
* [ifBlank](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/if-blank.html)
* [ifEmpty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/if-empty.html)
* [indexOf](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/index-of.html)
* [indexOfAny](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/index-of-any.html)
* [indexOfFirst](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/index-of-first.html)
* [indexOfLast](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/index-of-last.html)
* [isBlank]()
* [isEmpty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-empty.html)
* [isNotBlank]()
* [isNotEmpty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-not-blank.html)
* [isNullOrBlank](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-null-or-blank.html)
* [isNullOrEmpty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-null-or-empty.html)
* [iterator](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/iterator.html)
* [last](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/last.html)
* [lastIndexOf](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/last-index-of.html)
* [lastIndexOfAny](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/last-index-of-any.html)
* [lastOrNull](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/last-or-null.html)
* [lines](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/lines.html)
* [lineSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/line-sequence.html)
* [map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/map.html)
* [mapIndexed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/map-indexed.html)
* [mapIndexedNotNull](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/map-indexed-not-null.html)
* [mapIndexedNotNullTo](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/map-indexed-not-null-to.html)
* [mapIndexedTo](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/map-indexed-to.html)
* [mapNotNull](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/map-not-null.html)
* [mapNotNullTo](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/map-not-null-to.html)
* [mapTo](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/map-to.html)
* [max](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/max.html)
* [maxBy](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/max-by.html)
* [maxWith](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/max-with.html)
* [min](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/min.html)
* [minBy](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/min-by.html)
* [minWith](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/min-with.html)
* [none](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/none.html)
* [onEach](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/on-each.html)
* [orEmpty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/or-empty.html)
* [padEnd](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/pad-end.html)
* [padStart](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/pad-start.html)
* [partition](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/partition.html)
* [prependIndent](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/prepend-indent.html)
* [random](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/random.html)
* [reader](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/reader.html)
* [reduce](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/reduce.html)
* [reduceIndexed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/reduce-indexed.html)
* [reduceRight](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/reduce-right.html)
* [reduceRightIndexed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/reduce-right-indexed.html)
* [regionMatches](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/region-matches.html)
* [removePrefix](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/remove-prefix.html)
* [removeRange](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/remove-range.html)
* [removeSuffix](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/remove-suffix.html)
* [removeSurrounding](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/remove-surrounding.html)
* [replace](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/replace.html)
* [replaceAfter](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/replace-after.html)
* [replaceAfterLast](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/replace-after-last.html)
* [replaceBefore](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/replace-before.html)
* [replaceBeforeLast](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/replace-before-last.html)
* [replaceFirst](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/replace-first.html)
* [replaceIndent](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/replace-indent.html)
* [replaceIndentByMargin](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/replace-indent-by-margin.html)

##  Example

```java
import static io.github.linktosriram.kext4j.text.StringUtils.*;

map("hello world", Character::toUpperCase);     // [H, E, L, L, O,  , W, O, R, L, D]
filter("Hello World", Character::isUpperCase);  // "HW"
groupBy("Hello World", Character::isUpperCase); // {true=[H, W], false=[e, l, l, o,  , o, r, l, d]}
```

## License

This project is licensed under the MIT license. See the [LICENSE](LICENSE) file for more info.

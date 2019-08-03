# kext4j

Kotlin Extension Functions for Java.

Implementation of kotlin extension methods for Java.

Currently in-progress:
* https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html

##  Example

```java
import static io.github.linktosriram.kext4j.text.StringUtils.*;

map("hello world", Character::toUpperCase);     // [H, E, L, L, O,  , W, O, R, L, D]
filter("Hello World", Character::isUpperCase);  // "HW"
groupBy("Hello World", Character::isUpperCase); // {true=[H, W], false=[e, l, l, o,  , o, r, l, d]}
```

## License

This project is licensed under the MIT license. See the [LICENSE](LICENSE) file for more info.

# recordtuples

[![Java CI](https://github.com/FilipMalczak/recordtuples/actions/workflows/ci.yaml/badge.svg)](https://github.com/FilipMalczak/recordtuples/actions/workflows/ci.yaml)

Typed tuples based on records for Java. As simple as that.

Tuples are a tricky thing. Some love them, some hate them. Nontheless, there is a niche on the market that for a long
time has been filled by [javatuples](https://github.com/javatuples/javatuples).

Unfortunately, that project is a bit stale. JDK14 introduced records, which are a perfect tool to implement tuples.
So, here we go.

Most of the sources of this project are generated. Have a look at [recordDefinition(...) method in the buildscript](./build.gradle).

No Javadoc, no tutorials, nothing. It is what you can expect. An example of sources for `Pair` can be found at the end
of this README.

Names are mimicking Javatuples naming, but we only provide to 8-element tuple and we add an empty one. They (the names) are:
- `Empty` 
- `Unit`
- `Pair`
- `Triplet`
- `Quartet`
- `Quintet`
- `Sextet`
- `Septet`
- `Octet`

Components are generic (without any boundaries) and are nullable. They are named `vX` (where `X` is index, starting with 0) 
and the generic types for them are called `TX`.

Each tuple supports:
- `of(...)` static method (fluent contructor; its nicer to say `Pair.of(1, 2)` than `new Pair<>(1, 2)`)
- `reverse()` method (that returns the tuple in reverse order, e.g. `(a, b, c)` → `(c, b, a)`)

All of them, besides `Empty` (for which it wouldn't make any sense) also have:
- `getX()` methods (accessors to components)
- `withX(val)` methods (setters producing an instance with single component replaced)
- `mapX(Function<TX, T> mapper)` methods (which applies given function to xth component and return a tuple with that component
  replaced by the outcome)

Besides, there is a [Tuple interface](src/main/java/com/github/filipmalczak/recordtuples/Tuple.java) implemented by all 
the records. If has 2 generic parameters: `Head` (describing the type of the first element) and `Tail extends Tuple` (the type of
tuple consisting of all the elements but the first). For example `Triplet<T0, T1, T2>(...) implements Tuple<T0, Pair<T1, T2>>`.
That interface defines quite obvious `getHead()` and `getTail()` methods, as well as `size()` one.

That is the exact reason for introducing `Empty` record. Without it `Unit` couldn't have had defined the tail type. It 
itself also implements `Tuple<Void, Empty>` - its head is always null. For consistency `Empty` also has `of()` and `reverse()`
methods. It is a soft singleton - as long as you don't use the constructor explicitly, `of()` will return static instance,
and `reverse()` and `getTail()` always return `this`.

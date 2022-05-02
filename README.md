# recordtuples

Typed tuples based on records for Java. As simple as that.

Tuples are a tricky thing. Some love them, some hate them. Nontheless, there is a niche on the market that for a long
time has been filled by [javatuples](https://github.com/javatuples/javatuples).

Unfortunately, that project is a bit stale. JDK14 introduced records, which are a perfect tool to implement tuples.
So, here we go.

All the sources of this project are generated. Have a look at [recordDefinition(...) method in the buildscript](./build.gradle).

No Javadoc, no tutorials, nothing. It is what you can expect. For example, 3-element tuple looks like this:

    public record Triplet<T0, T1, T2>(
    T0 v0,
    T1 v1,
    T2 v2
    ){
        public T0 get0() { return v0; }
        public T1 get1() { return v1; }
        public T2 get2() { return v2; }
    
        public static <T0, T1, T2> Triplet<T0, T1, T2> of(T0 v0, T1 v1, T2 v2){
            return new Triplet<>(v0, v1, v2);
        }
        
        public <T> Triplet<T, T1, T2> with0(T v) { return of(v, v1, v2); }
        public <T> Triplet<T0, T, T2> with1(T v) { return of(v0, v, v2); }
        public <T> Triplet<T0, T1, T> with2(T v) { return of(v0, v1, v); }
    }

Components are generic (without any boundaries) and are nullable. They are named `vX` (where `X` is index, starting with 0) 
and the generic types for them are called `TX`.

Each tuple supports:
-`getX` methods (accessors to components), 
-`withX(val)` methods (setters producing an instance with single component replaced)
- `of(...)` static method (fluent contructor; its nicer to say `Pair.of(1, 2)` than `new Pair<>(1, 2)`).

Names are mimicking Javatuples naming, but we only provide to 8-element tuple. They (the names) are:
- `Unit`
- `Pair`
- `Triplet`
- `Quartet`
- `Quintet`
- `Sextet`
- `Septet`
- `Octet`
# recordtuples

Typed tuples based on records for Java. As simple as that.

Tuples are a tricky thing. Some love them, some hate them. Nontheless, there is a niche on the market that for a long
time has been filled by [javatuples](https://github.com/javatuples/javatuples).

Unfortunately, that project is a bit stale. JDK14 introduced records, which are a perfect tool to implement tuples.
So, here we go.

All the sources of this project are generated. Have a look at [recordDefinition(...) method in the buildscript](./build.gradle).

No Javadoc, no tutorials, nothing. It is what you can expect. For example, 2-element tuple looks like this:

    public record Pair<T0, T1>(
    T0 v0,
    T1 v1
    ){
        public static <T0, T1> Pair<T0, T1> of(T0 v0, T1 v1){
            return new Pair<>(v0, v1);
        }
    
        public Pair<T1, T0> reverse(){ return of(v1, v0); }
    
        public T0 get0() { return v0; }
        public T1 get1() { return v1; }
    
        public <T> Pair<T, T1> with0(T v) { return of(v, v1); }
        public <T> Pair<T0, T> with1(T v) { return of(v0, v); }
    
        public <T> Pair<T, T1> map0(Function<T0, T> mapper) { return with0(mapper.apply(v0)); }
        public <T> Pair<T0, T> map1(Function<T1, T> mapper) { return with1(mapper.apply(v1)); }
    }

Names are mimicking Javatuples naming, but we only provide to 8-element tuple. They (the names) are:
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
- `getX` methods (accessors to components)
- `withX(val)` methods (setters producing an instance with single component replaced)
- `mapX(Function<TX, T> mapper)` methods (which applies given function to xth component and return a tuple with that component
  replaced by the outcome)
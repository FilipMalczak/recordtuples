# recordtuples

[![Java CI](https://github.com/FilipMalczak/recordtuples/actions/workflows/ci.yaml/badge.svg)](https://github.com/FilipMalczak/recordtuples/actions/workflows/ci.yaml)
[![](https://jitpack.io/v/FilipMalczak/recordtuples.svg)](https://jitpack.io/#FilipMalczak/recordtuples)

Typed tuples based on records for Java. As simple as that.

> A summary of the project comes first; then you'll find usage instructions and at the end there is a code snippet that
> may be pretty explanatory.

## Details

Tuples are a tricky thing. Some love them, some hate them. Nontheless, there is a niche on the market that for a long
time has been filled by [javatuples](https://github.com/javatuples/javatuples).

Unfortunately, that project is a bit stale. JDK14 introduced (and JDK16 stabilized) records, which are a perfect tool to implement tuples.
So, here we go.

Most of the sources of this project are generated. [This](buildSrc/src/main/groovy/com/filipmalczak/recordtuples/TupleSource.groovy) 
is the class used to generate sources of each tuple, and 
[this one](buildSrc/src/main/groovy/com/filipmalczak/recordtuples/ComparatorsSource.groovy)
generates all the comparators.

No Javadoc, no tutorials, nothing. It is what you can expect. An example of sources for `Triplet` can be found at the end
of this README.

All the classes are in the `com.github.filipmalczak.recordtuples` package. 
[CI](https://github.com/FilipMalczak/recordtuples/actions) is configured to print all the generated sources in a dedicated
job, so if you wanna look up how the sources look like, look into "Print sources" step.

### Tuples

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

#### Tuple interface

Besides, there is a [Tuple interface](src/main/java/com/github/filipmalczak/recordtuples/Tuple.java) implemented by all 
the records. If has 2 generic parameters: `Head` (describing the type of the first element) and `Tail extends Tuple` (the type of
tuple consisting of all the elements but the first). For example `Triplet<T0, T1, T2>(...) implements Tuple<T0, Pair<T1, T2>>`.
That interface defines quite obvious `getHead()` and `getTail()` methods, as well as `size()` one.

That is the exact reason for introducing `Empty` record. Without it `Unit` couldn't have had defined the tail type. It 
itself also implements `Tuple<Void, Empty>` - its head is always null. For consistency `Empty` also has `of()` and `reverse()`
methods. It is a soft singleton - as long as you don't use the constructor explicitly, `of()` will return static instance,
and `reverse()` and `getTail()` always return `this`.

### Comparing

Making tuples `Comparable` would not be trivial, to say the least. Instead we provide a set of common comparators. 
You can find them in `TupleComparators` utility class. It has static utility classes for each non-empty tuple type, so the
general structure looks like

    public final class TupleComparators {
        private TupleComparators() {}
    
        public static final class Units {
            private Units() {}
    
            (...)// comparator factories
        }

        public static final class Pairs {
            private Units() {}
    
            (...)// comparator factories
        }

        (...)//Triplets, Quartets and so on
    }

For each type, there are following methods:

    static <T> Comparator<TupleType<Generics>> comparingX(Comparator<T> delegate){
        return (u1, u2) -> delegate.compare(u1.get0(), u2.get0());
    }
    
    static <T extends Comparable<T>> Comparator<TupleType<...>> comparingX(){
        return comparing0(Comparable::compareTo);
    }

where `TupleType` is quite obvious (`Unit`, `Pair`, `Triplet` and so on), `X` is field index and `Generics` are all the 
generic parameters specified as `?`, besides the `X`th, which is specified to be `T`, for example:

    static <T extends Comparable<T>> Comparator<Triplet<?, T, ?>> comparing1(){
        return comparing1(Comparable::compareTo);
    }

Last, but not least, for each type we also have `naturalOrdering()` comparator, that tries comparing by the first field,
if they are equal proceeds to compare to the second field, then to third, etc. 
In other words, `naturalOrdering().compare(Triplet.of(1, 1, 1), Triplet.of(1, 1, 2)) < 0`. It requires that all the
component types are comparable. For example for pairs it looks like:

    static <T0 extends Comparable<T0>, T1 extends Comparable<T1>> Comparator<Pair<T0, T1>> naturalOrdering(){
        return (u1, u2) -> {
            int by0 = u1.v0().compareTo(u2.v0());
            if (by0 != 0) return by0;
    
              return u1.v1().compareTo(u2.v1());
        };
    }

> If you think that natural ordering with custom comparators for each field would be useful, please open a GitHub issue;
> I'd be happy to look into it, but I don't think its worth the effort.

## Using it

To start using this you'll need JDK16+ since it exploits records.

> Both JitPack and GitHub Actions are using JDK17, as it is the earliest LTS that supports records. You shouldn't
> linger with updating, so go for it.

Hosting is handled via [jitpack](https://jitpack.io/#FilipMalczak/recordtuples).

Current version is [0.3.0-SNAPSHOT](https://github.com/FilipMalczak/recordtuples/tree/0.3.0).

> 0.1.0 used JDK14 with enabled preview features, which was a mistake. Bumped the version to 0.2.0 instead of 0.1.1 for
> clarity, when forcing usage of JDK16+.

> TODO make some kind of a changelog

### Gradle

    allprojects {
      repositories {
        ...
        maven { url 'https://jitpack.io' }
      }
    }
    
    dependencies {
      implementation 'com.github.FilipMalczak:recordtuples:0.3.0-SNAPSHOT'
    }

### Maven

    <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
    
    (...)
    
    <dependency>
	    <groupId>com.github.FilipMalczak</groupId>
	    <artifactId>recordtuples</artifactId>
	    <version>0.3.0-SNAPSHOT</version>
	</dependency>

### Others

Look it up on [jitpack](https://jitpack.io/#FilipMalczak/recordtuples).

## Example source of `Triplet`

    public record Triplet<T0, T1, T2>(
        T0 v0,
        T1 v1,
        T2 v2
    ) implements Tuple<T0, Pair<T1, T2>> {
        public static <T0, T1, T2> Triplet<T0, T1, T2> of(T0 v0, T1 v1, T2 v2){
            return new Triplet<>(v0, v1, v2);
        }
    
        @Override
        public T0 getHead(){ return v0; }
        
        @Override
        public Pair<T1, T2> getTail(){ return Pair.of(v1, v2); }
    
        @Override
        public int size(){ return 3; }
        
        public Triplet<T2, T1, T0> reverse(){ return of(v2, v1, v0); }
    
        public T0 get0() { return v0; }
        public T1 get1() { return v1; }
        public T2 get2() { return v2; }
    
        public <T> Triplet<T, T1, T2> with0(T v) { return of(v, v1, v2); }
        public <T> Triplet<T0, T, T2> with1(T v) { return of(v0, v, v2); }
        public <T> Triplet<T0, T1, T> with2(T v) { return of(v0, v1, v); }
    
        public <T> Triplet<T, T1, T2> map0(Function<T0, T> mapper) { return with0(mapper.apply(v0)); }
        public <T> Triplet<T0, T, T2> map1(Function<T1, T> mapper) { return with1(mapper.apply(v1)); }
        public <T> Triplet<T0, T1, T> map2(Function<T2, T> mapper) { return with2(mapper.apply(v2)); }
    }

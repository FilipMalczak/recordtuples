# recordtuples

[![Java CI](https://github.com/FilipMalczak/recordtuples/actions/workflows/ci.yaml/badge.svg)](https://github.com/FilipMalczak/recordtuples/actions/workflows/ci.yaml)
[![](https://jitpack.io/v/FilipMalczak/recordtuples.svg)](https://jitpack.io/#FilipMalczak/recordtuples)

Typed tuples based on records for Java. As simple as that.

> A summary of the project comes first; then you'll find usage instructions and at the end there is a code snippet that
> may be pretty explanatory.

## Details

Tuples are a tricky thing. Some love them, some hate them. Nontheless, there is a niche on the market that for a long
time has been filled by [javatuples](https://github.com/javatuples/javatuples).

Unfortunately, that project is a bit stale. JDK14 introduced records, which are a perfect tool to implement tuples.
So, here we go.

Most of the sources of this project are generated. Have a look at [recordDefinition(...) method in the buildscript](./build.gradle).

No Javadoc, no tutorials, nothing. It is what you can expect. An example of sources for `Triplet` can be found at the end
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

## Using it

To start using this you'll need JDK14+ (and may need to tweak preview features on your own), since it exploits records.

Hosting is handled via [jitpack](https://jitpack.io/#FilipMalczak/recordtuples/v0.2.0).

Current version is [0.2.0](https://github.com/FilipMalczak/recordtuples/releases/tag/0.2.0) and isn't expected to be
bumped anytime soon (since there aren't many features that tuples can have).

> 0.1.0 used JDK14 with enabled preview features, which was a mistake. Bumped the version to 0.2.0 instead of 0.1.1 for
> clarity.

### Gradle

    allprojects {
      repositories {
        ...
        maven { url 'https://jitpack.io' }
      }
    }
    
    dependencies {
      implementation 'com.github.FilipMalczak:recordtuples:0.2.0'
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
	    <version>0.2.0</version>
	</dependency>

### Others

Look it up on [jitpack](https://jitpack.io/#FilipMalczak/recordtuples/0.2.0).

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
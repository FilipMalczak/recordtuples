package com.github.filipmalczak.recordtuples;

/**
 * Useful marker interface - allows for some neat generic tricks.
 *
 * For example, say you want to make a composable builder interfaces:
 * <pre>{@code
 * interface WithFoo<S extends WithFoo<S>> extends Self<S> {
 *     S setFoo(int foo);
 * }
 *
 * interface WithBar<S extends WithBar<S>> extends Self<S> {
 *     S setBar(String bar);
 * }
 *
 * interface FooBarBuilder extends WithFoo<FooBarBuilder>, WithBar<FooBarBuilder> {}
 * }</pre>
 *
 * Now consider that:
 *
 * <pre>{@code
 * FooBarBuilder builder = ...;
 * builder           // (1)
 *   .setFoo(1)      // (2)
 *   .setBar("baz"); // (3)
 * }</pre>
 *
 * (1) is obviously of type {@code FooBarBuilder}. Now, if {@code WithFoo} didn't have generic {@code S} argument and
 * {@code setFoo} simply returned {@code WithFoo}, then at (2) the type would be {@code WithFoo}, which has no
 * {@code setBar} method, so you'd get an error. This way {@code setFoo} knows to return {@code FooBarBuilder}, so
 * {@code setBar} or (3) also resolves to {@code FooBarBuilder}.
 *
 * @param <T> either a generic placeholder for composable supertypes representing the type that will extend or implement
 *           them or as concrete type of {@code this} as possible for concrete classes
 */
public interface Self<T extends Self<T>> {
    default T self(){
        return (T) this;
    }
}

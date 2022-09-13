package com.github.filipmalczak.recordtuples;

import java.util.Optional;

public class StringStringUnion implements HasUnionLikeProperties<StringStringUnion> {
    private String foo;
    private Optional<String> bar = Optional.empty();

    public StringStringUnion(String foo, Optional<String> bar) {
        this.foo = foo;
        this.bar = bar;
    }

    public StringStringUnion(String foo, String bar) {
        this.foo = foo;
        this.bar = Optional.ofNullable(bar);
    }

    public String getFoo() {
        return foo;
    }

    public Optional<String> getBar() {
        return bar;
    }

    public StringStringUnion withFoo(String foo){
        return new StringStringUnion(foo, bar);
    }

    public StringStringUnion withBar(String bar){
        return new StringStringUnion(foo, bar);
    }

    public StringStringUnion withBar(Optional<String> bar){
        return new StringStringUnion(foo, bar);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringStringUnion that = (StringStringUnion) o;

        if (foo != null ? !foo.equals(that.foo) : that.foo != null) return false;
        return bar != null ? bar.equals(that.bar) : that.bar == null;
    }

    @Override
    public int hashCode() {
        int result = foo != null ? foo.hashCode() : 0;
        result = 31 * result + (bar != null ? bar.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TwoFieldUnion{" +
                "foo='" + foo + '\'' +
                ", bar='" + bar + '\'' +
                '}';
    }
}

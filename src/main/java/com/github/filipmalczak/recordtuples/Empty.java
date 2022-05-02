package com.github.filipmalczak.recordtuples;

public record Empty() implements Tuple<Void, Empty> {
    private final static Empty INSTANCE = new Empty();

    //it may sound weird when read out loud, but its consistent with other tuples
    public static Empty of() {
        return INSTANCE;
    }

    //again, doesn't make any sense, but for consistency we provide it
    public Empty reverse(){ return this; }

    @Override
    public Void getHead() {
        return null;
    }

    @Override
    public Empty getTail() {
        return this;
    }

    @Override
    public int size() {
        return 0;
    }
}

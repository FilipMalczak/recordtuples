package com.github.filipmalczak.recordtuples;

public interface Tuple<Head, Tail extends Tuple> {
    Head getHead();
    Tail getTail();
    int size();
}

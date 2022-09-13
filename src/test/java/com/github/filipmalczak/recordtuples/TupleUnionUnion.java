package com.github.filipmalczak.recordtuples;

import java.util.Optional;

public record TupleUnionUnion(Tuple tuple, StringStringUnion union) implements HasUnionLikeProperties<TupleUnionUnion> {
    public TupleUnionUnion withTuple(Tuple tuple){
        return new TupleUnionUnion(tuple, union);
    }

    public TupleUnionUnion withUnion(StringStringUnion union){
        return new TupleUnionUnion(tuple, union);
    }

    public static TupleUnionUnion empty(){
        return new TupleUnionUnion(Empty.of(), new StringStringUnion(null, Optional.empty()));
    }
}
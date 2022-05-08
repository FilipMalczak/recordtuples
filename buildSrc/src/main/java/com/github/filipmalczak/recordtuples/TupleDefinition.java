package com.github.filipmalczak.recordtuples;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public record TupleDefinition(String name, String tailName, int size) {
    public static List<TupleDefinition> load(Function<String, String> propertyResolver){
        return Stream.of(propertyResolver.apply("tuples").split(","))
            .map(
                name -> new TupleDefinition(
                    name,
                    propertyResolver.apply("tuples."+name+".tail"),
                    Integer.parseInt(propertyResolver.apply("tuples."+name+".size"))
                )
            )
            .toList();
    }
}

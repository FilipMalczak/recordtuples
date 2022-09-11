package com.github.filipmalczak.recordtuples;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * Implement this to mark a type that you expect to be used as most of its fields empty. It will add a method that looks
 * through this instances fields and prepares a report on whether the field is considered empty or not. By default it
 * takes allDeclaredFields of this.getClass() but you can override a method to customize it. It also understands Optionals
 * and Streams (checks isEmpty() or count() == 0, respectively), but you can change that with overloads as well.
 *
 * By default it expects exactly 1 field. It is controlled by inclusive lower and upper limit, which are both 1, thus such
 * behaviour. You can change lower limit to 0 for optional unions or go nuts and produce "between 2 and 5 values" kind of field.
 *
 * Non empty fields can be streamed, both with more details (like which emptiness checker considered a value of which field empty)
 * or just as a stream of values of non-empty fields
 *
 * @param <S>
 */
public interface HasUnionLikeProperties<S extends HasUnionLikeProperties<S>> extends Self<S> {
    interface EmptinessChecker {
        String name();
        boolean isEmpty(Object val);

        EmptinessChecker NULLNESS = new EmptinessChecker() {
            @Override
            public String name() {
                return "Check against explicit null";
            }

            @Override
            public boolean isEmpty(Object val) {
                return val == null;
            }
        };

        EmptinessChecker OPTIONALS = new EmptinessChecker() {
            @Override
            public String name() {
                return "Check optional.isEmpty()";
            }

            @Override
            public boolean isEmpty(Object val) {
                return ((Optional) val).isEmpty();
            }
        };

        EmptinessChecker STREAM = new EmptinessChecker() {
            @Override
            public String name() {
                return "Counts elements of stream and requires the number to be 0";
            }

            @Override
            public boolean isEmpty(Object val) {
                return ((Stream) val).count() == 0;
            }
        };
    }

    interface Getter<S, T> {
        T get(S object);

        static Getter of(Field field){
            return o -> safeGet(field, o);
        }

        private static Object safeGet(Field field, Object o){
            try {
                return field.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
    record FieldDefinition<S>(String name, Getter<S, Object> getter){}

    //todo add @Ignore and @Include (for methods)
    record MatchResults<S>(S on, FieldDefinition<S> of, List<EmptinessChecker> against){
        Stream<EmptinessChecker> checkersClaimingEmptiness(){
            return against.stream().filter(c -> c.isEmpty(of.getter.get(on)));
        }

        boolean isEmpty(){
            return checkersClaimingEmptiness().findFirst().isPresent();
        }
    }
    record UnionLikeView<Self>(Self of, List<MatchResults<Self>> results, int minLimit, int maxLimit){
        Optional findFirst(){
            return findAll().findFirst();
        }

        Stream findAll(){
            return findNonEmptyResults().map(r -> r.of.getter.get(r.on));
        }

        Stream<MatchResults<Self>> findNonEmptyResults(){
            return results.stream().filter(r -> !r.isEmpty());
        }

        long nonEmptyFieldCount(){
            return findNonEmptyResults().count();
        }

        boolean isValid(){
            var s = nonEmptyFieldCount();
            return s >= minLimit & s <= maxLimit;
        }
    }

    default int minFieldsSet(){ return 1; }
    default int maxFieldsSet(){ return 1; }
    default UnionLikeView<S> unionLikView(){ return unionLikView(maxFieldsSet()); }
    default UnionLikeView<S> unionLikView(int maxFields){ return unionLikView(minFieldsSet(), maxFields); }
    default UnionLikeView<S> unionLikView(int minFields, int maxFields){
        var fields = fieldsThatMatterForUnion().toList();
        var results = fields.stream().map(f ->
                new MatchResults<S>(self(), f, checkersOf(f.getter().get(self())))
            )
            .toList();
        return new UnionLikeView<>(
            self(),
            results,
            minFields,
            maxFields
        );
    }

    default <T> List<EmptinessChecker> checkersOf(Object o){
        var out = new ArrayList<EmptinessChecker>(3);
        out.add(EmptinessChecker.NULLNESS);
        if (o instanceof Optional)
            out.add(EmptinessChecker.OPTIONALS);
        if (o instanceof Stream<?>)
            out.add(EmptinessChecker.STREAM);
        return out;
    }

    default Stream<FieldDefinition> fieldsThatMatterForUnion(){
        return Stream.of(this.getClass().getDeclaredFields())
            .map(f -> { f.setAccessible(true); return f; })
            .map(f ->
                new FieldDefinition(
                    f.getName(),
                    Getter.of(f)
                )
            );
    }
}

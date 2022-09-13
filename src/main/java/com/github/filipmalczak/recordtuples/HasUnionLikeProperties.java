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
 * Streams (checks isEmpty() or count() == 0, respectively) and Tuples (size needs to be 0, so it allows for custom tuple
 * implementations), but you can change that with overloads as well.
 *
 * By default it expects exactly 1 field. It is controlled by inclusive lower and upper limit, which are both 1, thus such
 * behaviour. You can change lower limit to 0 for optional unions or go nuts and produce "between 2 and 5 values" kind of field.
 *
 * Non empty fields can be streamed, both with more details (like which emptiness checker considered a value of which field empty)
 * or just as a stream of values of non-empty fields.
 *
 * Caveat: fields of union-like type
 * First of all, they are considered empty if none of their fields have been set. Invalid cases (e.g. min number of
 * non-empty fields is 2 but only 1 is set) are not considered at all here.
 * Validity of these fields is considered when deciding validity of the union-like instance though. Besides the number
 * of non-empty fields satisfying the min/max criteria, all the non-empty fields that are union-like need to be valid
 * as well.
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

        EmptinessChecker UNIONS = new EmptinessChecker() {
            @Override
            public String name() {
                return "Checks that union-like type isEmpty()";
            }

            @Override
            public boolean isEmpty(Object val) {
                return ((HasUnionLikeProperties) val).unionLikView().isEmpty();
            }
        };

        EmptinessChecker TUPLES = new EmptinessChecker() {
            @Override
            public String name() {
                return "Checks that tuple size is 0";
            }

            @Override
            public boolean isEmpty(Object val) {
                return ((Tuple) val).size() == 0;
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
            var nonEmpties = findNonEmptyResults().toList();
            var s = nonEmpties.size();
            if (s < minLimit || s > maxLimit) return false;
            return nonEmpties.stream()
                    .map(r -> r.of().getter().get(r.on()))
                    .filter(o -> o instanceof HasUnionLikeProperties<?>)
                    .map(o -> ((HasUnionLikeProperties<?>) o).unionLikView())
                    .allMatch(UnionLikeView::isValid);
        }

        boolean isEmpty(){
            return findFirst().isEmpty();
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
        if (o instanceof HasUnionLikeProperties<?>)
            out.add(EmptinessChecker.UNIONS);
        if (o instanceof Tuple<?,?>)
            out.add(EmptinessChecker.TUPLES);
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

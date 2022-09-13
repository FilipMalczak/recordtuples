package com.github.filipmalczak.recordtuples;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HasUnionLikePropertiesTest {
    @Nested
    public class StringStringTests {
        StringStringUnion stringStringUnion;

        @BeforeEach
        void setup() {
            stringStringUnion = new StringStringUnion(null, Optional.empty());
        }

        @Test
        void emptyIsInvalid() {
            assertTrue(stringStringUnion.unionLikView().isEmpty());
            assertFalse(stringStringUnion.unionLikView().isValid());
        }

        @Test
        void justFooIsValid() {
            stringStringUnion = stringStringUnion.withFoo("foo");
            assertFalse(stringStringUnion.unionLikView().isEmpty());
            assertTrue(stringStringUnion.unionLikView().isValid());
        }

        @Test
        void justBarIsValid() {
            stringStringUnion = stringStringUnion.withBar("bar");
            assertFalse(stringStringUnion.unionLikView().isEmpty());
            assertTrue(stringStringUnion.unionLikView().isValid());
        }

        @Test
        void bothIsInvalid() {
            stringStringUnion = stringStringUnion.withFoo("foo").withBar("bar");
            assertFalse(stringStringUnion.unionLikView().isEmpty());
            assertFalse(stringStringUnion.unionLikView().isValid());
        }
    }

    @Nested
    public class TupleUnionTests {
        TupleUnionUnion tupleUnionUnion;

        @BeforeEach
        void setup(){
            tupleUnionUnion = TupleUnionUnion.empty();
        }

        @Test
        void emptiesAreEmpty(){
            assertTrue(tupleUnionUnion.unionLikView().isEmpty());
            assertFalse(tupleUnionUnion.unionLikView().isValid());
        }

        @Test
        void fooIsEnough(){
            tupleUnionUnion = tupleUnionUnion
                .withUnion(
                    tupleUnionUnion.union()
                        .withFoo("foo")
                );
            assertFalse(tupleUnionUnion.unionLikView().isEmpty());
            assertTrue(tupleUnionUnion.unionLikView().isValid());
        }

        @Test
        void barIsEnough(){
            tupleUnionUnion = tupleUnionUnion
                .withUnion(
                    tupleUnionUnion.union()
                        .withBar("bar")
                );
            assertFalse(tupleUnionUnion.unionLikView().isEmpty());
            assertTrue(tupleUnionUnion.unionLikView().isValid());
        }

        @Test
        void pairIsEnough(){
            tupleUnionUnion = tupleUnionUnion
                .withTuple(
                    Pair.of(1, 2)
                );
            assertFalse(tupleUnionUnion.unionLikView().isEmpty());
            assertTrue(tupleUnionUnion.unionLikView().isValid());
        }

        @Test
        void fooBarIsInvalidEvenThoughTupleEmpty(){
            tupleUnionUnion = tupleUnionUnion
                .withUnion(
                    tupleUnionUnion.union()
                        .withFoo("foo")
                        .withBar("bar")
                );
            assertFalse(tupleUnionUnion.unionLikView().isEmpty());
            assertFalse(tupleUnionUnion.unionLikView().isValid());
        }

        @Test
        void pairAndFooIsInvalid(){
            tupleUnionUnion = tupleUnionUnion
                .withTuple(
                    Pair.of(1, 2)
                )
                .withUnion(
                    tupleUnionUnion.union()
                        .withFoo("foo")
                );
            assertFalse(tupleUnionUnion.unionLikView().isEmpty());
            assertFalse(tupleUnionUnion.unionLikView().isValid());
        }
        @Test
        void pairAndBarIsInvalid(){
            tupleUnionUnion = tupleUnionUnion
                .withTuple(
                    Pair.of(1, 2)
                )
                .withUnion(
                    tupleUnionUnion.union()
                        .withBar("bar")
                );
            assertFalse(tupleUnionUnion.unionLikView().isEmpty());
            assertFalse(tupleUnionUnion.unionLikView().isValid());
        }
        @Test
        void pairFooAndBarIsInvalid(){
            tupleUnionUnion = tupleUnionUnion
                .withTuple(
                    Pair.of(1, 2)
                )
                .withUnion(
                    tupleUnionUnion.union()
                        .withFoo("foo")
                        .withBar("bar")
                );
            assertFalse(tupleUnionUnion.unionLikView().isEmpty());
            assertFalse(tupleUnionUnion.unionLikView().isValid());
        }

    }
}
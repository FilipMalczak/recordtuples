package com.github.filipmalczak.recordtuples;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HasUnionLikePropertiesTest {
    TwoFieldUnion twoFieldUnion;

    @BeforeEach
    void setup(){
        twoFieldUnion = new TwoFieldUnion(null, Optional.empty());
    }

    @Test
    void emptyIsInvalid(){
        assertFalse(twoFieldUnion.unionLikView().isValid());
    }

    @Test
    void justFooIsValid(){
        twoFieldUnion = twoFieldUnion.withFoo("foo");
        assertTrue(twoFieldUnion.unionLikView().isValid());
    }

    @Test
    void justBarIsValid(){
        twoFieldUnion = twoFieldUnion.withBar("bar");
        assertTrue(twoFieldUnion.unionLikView().isValid());
    }

    @Test
    void bothIsInvalid(){
        twoFieldUnion = twoFieldUnion.withFoo("foo").withBar("bar");
        assertFalse(twoFieldUnion.unionLikView().isValid());
    }
}
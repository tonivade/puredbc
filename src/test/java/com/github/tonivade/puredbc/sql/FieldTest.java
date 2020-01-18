/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldTest {

  Field<String> field = Field.of("name");
  Field<String> other = Field.of("other");

  @Test
  void field() {
    assertAll(
        () -> assertThrows(IllegalArgumentException.class, () -> Field.of(null)),
        () -> assertThrows(IllegalArgumentException.class, () -> Field.of("")),
        () -> assertEquals("a.name", field.alias("a").name()),
        () -> assertEquals("name as a", field.as("a").name()),
        () -> assertEquals("count(name)", field.count().name()),
        () -> assertEquals("sum(name)", field.sum().name()),
        () -> assertEquals("max(name)", field.max().name()),
        () -> assertEquals("min(name)", field.min().name()),
        () -> assertEquals("coalesce(name, 1)", field.coalesce(1).name()),
        () -> assertEquals(Condition.eq(field), field.eq()),
        () -> assertEquals(Condition.eq(field, other), field.eq(other)),
        () -> assertEquals(Condition.gt(field), field.gt()),
        () -> assertEquals(Condition.lt(field), field.lt()),
        () -> assertEquals(Condition.gte(field), field.gte()),
        () -> assertEquals(Condition.lte(field), field.lte()),
        () -> assertEquals(Condition.like(field), field.like()),
        () -> assertEquals(Condition.notEq(field), field.notEq())
    );
  }
}
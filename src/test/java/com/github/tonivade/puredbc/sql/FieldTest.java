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


  private final Example EXAMPLE = new Example();
  private final Example ALIAS = EXAMPLE.as("a");

  @Test
  void field() {
    assertAll(
        () -> assertThrows(IllegalArgumentException.class, () -> Field.of(EXAMPLE, null)),
        () -> assertThrows(IllegalArgumentException.class, () -> Field.of(EXAMPLE, "")),
        () -> assertEquals("name", EXAMPLE.field.name()),
        () -> assertEquals("name", ALIAS.field.name()),
        () -> assertEquals("example.name", EXAMPLE.field.fullName()),
        () -> assertEquals("a.name", ALIAS.field.fullName()),
        () -> assertEquals("name as a", EXAMPLE.field.as("a").name()),
        () -> assertEquals("count(example.name)", EXAMPLE.field.count().name()),
        () -> assertEquals("sum(example.name)", EXAMPLE.field.sum().name()),
        () -> assertEquals("max(example.name)", EXAMPLE.field.max().name()),
        () -> assertEquals("min(example.name)", EXAMPLE.field.min().name()),
        () -> assertEquals("avg(example.name)", EXAMPLE.field.avg().name()),
        () -> assertEquals("coalesce(example.name, 1)", EXAMPLE.field.coalesce(1).name()),
        () -> assertEquals(Condition.eq(EXAMPLE.field), EXAMPLE.field.eq()),
        () -> assertEquals(Condition.eq(EXAMPLE.field, EXAMPLE.other), EXAMPLE.field.eq(EXAMPLE.other)),
        () -> assertEquals(Condition.gt(EXAMPLE.field), EXAMPLE.field.gt()),
        () -> assertEquals(Condition.lt(EXAMPLE.field), EXAMPLE.field.lt()),
        () -> assertEquals(Condition.gte(EXAMPLE.field), EXAMPLE.field.gte()),
        () -> assertEquals(Condition.lte(EXAMPLE.field), EXAMPLE.field.lte()),
        () -> assertEquals(Condition.like(EXAMPLE.field), EXAMPLE.field.like()),
        () -> assertEquals(Condition.isNull(EXAMPLE.field), EXAMPLE.field.isNull()),
        () -> assertEquals(Condition.isNotNull(EXAMPLE.field), EXAMPLE.field.isNotNull()),
        () -> assertEquals(Condition.notEq(EXAMPLE.field), EXAMPLE.field.notEq())
    );
  }
}
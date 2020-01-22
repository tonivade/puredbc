/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import org.junit.jupiter.api.Test;

import static com.github.tonivade.puredbc.sql.Condition.eq;
import static com.github.tonivade.puredbc.sql.Condition.gt;
import static com.github.tonivade.puredbc.sql.Condition.gte;
import static com.github.tonivade.puredbc.sql.Condition.isNotNull;
import static com.github.tonivade.puredbc.sql.Condition.isNull;
import static com.github.tonivade.puredbc.sql.Condition.like;
import static com.github.tonivade.puredbc.sql.Condition.lt;
import static com.github.tonivade.puredbc.sql.Condition.lte;
import static com.github.tonivade.puredbc.sql.Condition.notEq;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConditionTest {

  private final Example EXAMPLE = new Example();
  private final Example ALIAS = EXAMPLE.as("a");

  @Test
  void condition() {
    assertAll(
        () -> assertThrows(IllegalArgumentException.class, () -> Condition.of(null)),
        () -> assertThrows(IllegalArgumentException.class, () -> Condition.of("")),
        () -> assertEquals("example.name = ?", eq(EXAMPLE.field).expression()),
        () -> assertEquals("not example.name = ?", eq(EXAMPLE.field).not().expression()),
        () -> assertEquals("example.name = example.other", eq(EXAMPLE.field, EXAMPLE.other).expression()),
        () -> assertEquals("example.name > ?", gt(EXAMPLE.field).expression()),
        () -> assertEquals("example.name < ?", lt(EXAMPLE.field).expression()),
        () -> assertEquals("example.name >= ?", gte(EXAMPLE.field).expression()),
        () -> assertEquals("example.name <= ?", lte(EXAMPLE.field).expression()),
        () -> assertEquals("example.name like ?", like(EXAMPLE.field).expression()),
        () -> assertEquals("example.name is null", isNull(EXAMPLE.field).expression()),
        () -> assertEquals("example.name is not null", isNotNull(EXAMPLE.field).expression()),
        () -> assertEquals("example.name <> ?", notEq(EXAMPLE.field).expression())
    );
  }
}
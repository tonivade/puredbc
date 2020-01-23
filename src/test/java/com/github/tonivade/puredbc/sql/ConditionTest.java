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

  final Field<String> name = Field.of("name");
  final Field<String> other = Field.of("other");

  @Test
  void condition() {
    assertAll(
        () -> assertThrows(IllegalArgumentException.class, () -> Condition.of(null)),
        () -> assertThrows(IllegalArgumentException.class, () -> Condition.of("")),
        () -> assertEquals("name as a", name.as("a").name()),
        () -> assertEquals("a.name", name.alias("a").name()),
        () -> assertEquals("name = ?", eq(name).expression()),
        () -> assertEquals("not name = ?", eq(name).not().expression()),
        () -> assertEquals("name = other", eq(name, other).expression()),
        () -> assertEquals("name > ?", gt(name).expression()),
        () -> assertEquals("name < ?", lt(name).expression()),
        () -> assertEquals("name >= ?", gte(name).expression()),
        () -> assertEquals("name <= ?", lte(name).expression()),
        () -> assertEquals("name like ?", like(name).expression()),
        () -> assertEquals("name is null", isNull(name).expression()),
        () -> assertEquals("name is not null", isNotNull(name).expression()),
        () -> assertEquals("name <> ?", notEq(name).expression())
    );
  }
}
/*
 * Copyright (c) 2020-2026, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.data.Range;
import org.junit.jupiter.api.Test;

import static com.github.tonivade.puredbc.sql.Condition.between;
import static com.github.tonivade.puredbc.sql.SQL.sql;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SQL1Test {

  final Field<Integer> field = Field.of("y");
  final Range range = Range.of(1, 10);

  @Test
  void bindIn() {
    SQL1<Iterable<Integer>> query = sql("select x from z").where(field.in());

    SQL sql = query.bind(range.collect());

    assertAll(
        () -> assertEquals("select x from z where y in (?, ?, ?, ?, ?, ?, ?, ?, ?)", sql.getQuery()),
        () -> assertEquals(arrayOf(range.collect()), sql.getParams())
    );
  }

  @Test
  void bindBetween() {
    SQL1<Range> query = sql("select x from z").where(between(field));

    SQL sql = query.bind(range);

    assertAll(
        () -> assertEquals("select x from z where y between ? and ?", sql.getQuery()),
        () -> assertEquals(arrayOf(range), sql.getParams())
    );
  }
}
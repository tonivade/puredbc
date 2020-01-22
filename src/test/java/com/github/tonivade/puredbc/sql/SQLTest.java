/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import org.junit.jupiter.api.Test;

import static com.github.tonivade.puredbc.sql.SQL.delete;
import static com.github.tonivade.puredbc.sql.SQL.insert;
import static com.github.tonivade.puredbc.sql.SQL.select;
import static com.github.tonivade.puredbc.sql.SQL.update;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLTest {

  private final Example EXAMPLE = new Example();

  @Test
  void test() {
    assertAll(
        () -> assertEquals("select example.name, example.other from example",
            select(EXAMPLE.all()).from(EXAMPLE).getQuery()),
        () -> assertEquals("insert into example (name, other) values (?, ?)",
            insert(EXAMPLE).values(EXAMPLE.field, EXAMPLE.other).bind("a", "b").getQuery()),
        () -> assertEquals("delete from example where example.name = ?",
            delete(EXAMPLE).where(EXAMPLE.field.eq()).bind("a").getQuery()),
        () -> assertEquals("update example set other = ? where example.name = ?",
            update(EXAMPLE).set(EXAMPLE.other).where(EXAMPLE.field.eq()).bind("a", "b").getQuery())
    );
  }
}
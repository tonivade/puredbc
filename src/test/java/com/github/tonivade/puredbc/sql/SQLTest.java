/*
 * Copyright (c) 2020-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.puredbc.sql.SQL.deleteFrom;
import static com.github.tonivade.puredbc.sql.SQL.insertInto;
import static com.github.tonivade.puredbc.sql.SQL.select;
import static com.github.tonivade.puredbc.sql.SQL.update;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.NonEmptyList;
import com.github.tonivade.purefun.typeclasses.TupleK2;

class SQLTest {

  private static final class Example implements Table2<Integer, String> {

    final Field<Integer> ID = Field.of("id");
    final Field<String> NAME = Field.of("name");

    @Override
    public String name() {
      return "example";
    }

    @Override
    public NonEmptyList<Field<?>> all() {
      return NonEmptyList.of(ID, NAME);
    }
    
    @Override
    public Tuple2<Integer, String> asTuple(Row row) {
      return Tuple2.of(row.getInteger(ID), row.getString(NAME));
    }
    
    @Override
    public TupleK2<Field_, Integer, String> fields() {
      return new TupleK2<>(ID, NAME);
    }
    
    @Override
    public Example as(String alias) {
      return this;
    }
  }

  private final Example EXAMPLE = new Example();

  @Test
  void test() {
    assertAll(
        () -> assertEquals("select id, name from example",
            select(EXAMPLE.all()).from(EXAMPLE).getQuery()),
        () -> assertEquals("insert into example (id, name) values (?, ?)",
            insertInto(EXAMPLE).values(EXAMPLE.ID, EXAMPLE.NAME).bind(1, "name").getQuery()),
        () -> assertEquals("delete from example where id = ?",
            deleteFrom(EXAMPLE).where(EXAMPLE.ID.eq()).bind(1).getQuery()),
        () -> assertEquals("update example set name = ? where id = ?",
            update(EXAMPLE).set(EXAMPLE.NAME).where(EXAMPLE.ID.eq()).bind("name", 1).getQuery())
    );
  }

}
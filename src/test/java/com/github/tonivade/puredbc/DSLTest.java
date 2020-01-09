/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.free.Free;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static com.github.tonivade.puredbc.DSL.query;
import static com.github.tonivade.puredbc.DSL.update;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;

public class DSLTest {

  @Test
  public void test() {
    Free<DSL.µ, Iterable<Tuple2<Integer, String>>> program = update("create table test (id int, name varchar(100))")
        .andThen(update("insert into test (id, name) values (?, ?)", arrayOf(1, "toni")))
        .andThen(query("select id, name from test", rs -> Tuple.of(rs.getInt("id"), rs.getString("name"))));

    Assertions.assertEquals(ImmutableList.of(Tuple.of(1, "toni")), DSL.run(program).apply(dataSource()));
  }

  private DataSource dataSource() {
    HikariConfig poolConfig = new HikariConfig();
    poolConfig.setJdbcUrl("jdbc:h2:mem:test");
    poolConfig.setUsername("sa");
    poolConfig.setPassword("");
    return new HikariDataSource(poolConfig);
  }
}

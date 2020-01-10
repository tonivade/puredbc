/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.puredbc.Bindable;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.free.Free;
import com.github.tonivade.purefun.type.Option;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.tonivade.puredbc.DSL.*;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DSLTest {

  @Test
  public void queryAll() {
    Free<DSL.µ, Iterable<Tuple2<Integer, String>>> program = 
      update(Bindable.of("create table if not exists test (id int primary key, name varchar(100))"))
        .andThen(update(Bindable.of("delete from test")))
        .andThen(update(Bindable.of("insert into test (id, name) values (?, ?)").with(1, "toni")))
        .andThen(query(Bindable.of("select id, name from test"), this::asTuple));

    assertEquals(ImmutableList.of(Tuple.of(1, "toni")), run(program).apply(dataSource()));
  }

  @Test
  public void queryJustOne() {
    Free<DSL.µ, Option<Tuple2<Integer, String>>> program = 
      update(Bindable.of("create table if not exists test (id int primary key, name varchar(100))"))
        .andThen(update(Bindable.of("delete from test")))
        .andThen(update(Bindable.of("insert into test (id, name) values (?, ?)").with(1, "toni")))
        .andThen(queryOne(Bindable.of("select id, name from test where id = ?").with(1), this::asTuple));

    assertEquals(Option.some(Tuple.of(1, "toni")), run(program).apply(dataSource()));
  }

  private DataSource dataSource() {
    HikariConfig poolConfig = new HikariConfig();
    poolConfig.setJdbcUrl("jdbc:h2:mem:test");
    poolConfig.setUsername("sa");
    poolConfig.setPassword("");
    return new HikariDataSource(poolConfig);
  }

  private Tuple2<Integer, String> asTuple(ResultSet rs) throws SQLException {
    return Tuple.of(rs.getInt("id"), rs.getString("name"));
  }
}

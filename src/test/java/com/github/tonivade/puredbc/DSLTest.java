/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.free.Free;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.tonivade.puredbc.DSL.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DSLTest {

  private final Bindable createTable = Bindable.of("create table if not exists test (id int primary key, name varchar(100))");
  private final Bindable dropTable = Bindable.of("drop table if exists test");
  private final Bindable deleteTable = Bindable.of("delete from test");
  private final Bindable2<Integer, String> insertRow = Bindable.of2("insert into test (id, name) values (?, ?)");
  private final Bindable findAll = Bindable.of("select id, name from test");
  private final Bindable1<Integer> findOne = Bindable.of1("select id, name from test where id = ?");

  @Test
  public void queryAll() {
    Free<DSL.µ, Iterable<Tuple2<Integer, String>>> program =
      update(createTable)
        .andThen(update(deleteTable))
        .andThen(update(insertRow.bind(1, "toni")))
        .andThen(query(findAll, this::asTuple));

    assertEquals(ImmutableList.of(Tuple.of(1, "toni")), unsafeRun(program).apply(dataSource()));
  }

  @Test
  public void queryJustOne() {
    Free<DSL.µ, Option<Tuple2<Integer, String>>> program =
        update(createTable)
            .andThen(update(deleteTable))
            .andThen(update(insertRow.bind(1, "toni")))
            .andThen(queryOne(findOne.bind(1), this::asTuple));

    assertEquals(Try.success(Option.some(Tuple.of(1, "toni"))), DSL.safeRun(program).apply(dataSource()));
  }

  @Test
  public void queryError() {
    Free<DSL.µ, Option<Tuple2<Integer, String>>> program =
        update(dropTable)
            .andThen(update(deleteTable))
            .andThen(update(insertRow.bind(1, "toni")))
            .andThen(queryOne(findOne.bind(1), this::asTuple));

    assertTrue(DSL.safeRun(program).apply(dataSource()).isFailure());
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

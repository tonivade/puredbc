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

import static com.github.tonivade.puredbc.Bindable.deleteFrom;
import static com.github.tonivade.puredbc.Bindable.insertInto;
import static com.github.tonivade.puredbc.Bindable.select;
import static com.github.tonivade.puredbc.Bindable.sql;
import static com.github.tonivade.puredbc.Bindable.update;
import static com.github.tonivade.puredbc.DSL.query;
import static com.github.tonivade.puredbc.DSL.queryOne;
import static com.github.tonivade.puredbc.DSL.safeRun;
import static com.github.tonivade.puredbc.DSL.unsafeRun;
import static com.github.tonivade.puredbc.DSL.update;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DSLTest {

  private final Bindable createTable = sql("create table if not exists test (id int primary key, name varchar(100))");
  private final Bindable dropTable = sql("drop table if exists test");
  private final Bindable deleteTable = deleteFrom("test");
  private final Bindable1<Integer> deleteTableWhere = deleteFrom("test").where("id = ?");
  private final Bindable2<Integer, String> insertRow = insertInto("test").values("id", "name");
  private final Bindable2<String, Integer> updateRow = update("test").<String>set("name").where("id = ?");
  private final Bindable findAll = select("id", "name").from("test");
  private final Bindable1<Integer> findOne = select("id", "name").from("test").where("id = ?");

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
            .andThen(update(deleteTableWhere.bind(1)))
            .andThen(update(insertRow.bind(1, "toni")))
            .andThen(update(updateRow.bind("pepe", 1)))
            .andThen(queryOne(findOne.bind(1), this::asTuple));

    assertEquals(Try.success(Option.some(Tuple.of(1, "pepe"))), safeRun(program).apply(dataSource()));
  }

  @Test
  public void queryError() {
    Free<DSL.µ, Option<Tuple2<Integer, String>>> program =
        update(dropTable)
            .andThen(update(deleteTable))
            .andThen(update(insertRow.bind(1, "toni")))
            .andThen(queryOne(findOne.bind(1), this::asTuple));

    assertAll(
        () -> assertThrows(SQLException.class, () -> unsafeRun(program).apply(dataSource())),
        () -> assertTrue(safeRun(program).apply(dataSource()).isFailure())
    );
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

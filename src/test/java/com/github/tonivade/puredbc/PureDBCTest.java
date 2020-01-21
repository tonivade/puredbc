/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.puredbc.sql.Row;
import com.github.tonivade.puredbc.sql.SQL;
import com.github.tonivade.puredbc.sql.SQL1;
import com.github.tonivade.puredbc.sql.SQL2;
import com.github.tonivade.puredbc.sql.Table2;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.NonEmptyList;
import com.github.tonivade.purefun.data.Range;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.purefun.typeclasses.For;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

import static com.github.tonivade.puredbc.PureDBC.queryIterable;
import static com.github.tonivade.puredbc.PureDBC.queryOne;
import static com.github.tonivade.puredbc.PureDBC.update;
import static com.github.tonivade.puredbc.PureDBC.updateWithKeys;
import static com.github.tonivade.puredbc.sql.Condition.between;
import static com.github.tonivade.puredbc.sql.SQL.delete;
import static com.github.tonivade.puredbc.sql.SQL.insert;
import static com.github.tonivade.puredbc.sql.SQL.select;
import static com.github.tonivade.puredbc.sql.SQL.update;
import static com.github.tonivade.puredbc.sql.SQL.sql;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static com.github.tonivade.purefun.data.Sequence.listOf;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PureDBCTest {

  private static final TestTable TEST = new TestTable();
  private static final TestTable ALIAS = TEST.as("a");

  private final DataSource dataSource = dataSource();

  private final SQL createTable = sql(
      "create table if not exists test(",
      "id identity primary key,",
      "name varchar(100))");
  private final SQL dropTable = sql("drop table if exists test");
  private final SQL deleteAll = delete(TEST);
  private final SQL1<Integer> deleteOne = delete(TEST).where(TEST.ID.eq());
  private final SQL1<String> insertRowWithKey = insert(TEST).values(TEST.NAME);
  private final SQL2<Integer, String> insertRow = insert(TEST).values(TEST.ID, TEST.NAME);
  private final SQL2<String, Integer> updateRow = update(TEST).set(TEST.NAME).where(TEST.ID.eq());
  private final SQL findAll = select(ALIAS.all()).from(ALIAS);
  private final SQL1<Iterable<Integer>> findIn = select(ALIAS.all()).from(ALIAS).where(ALIAS.ID.in());
  private final SQL1<Range> findBetween = select(ALIAS.all()).from(ALIAS).where(between(ALIAS.ID));
  private final SQL count = select(TEST.ID.count().as("elements")).from(TEST);
  private final SQL1<Integer> findOne = select(TEST.ID, TEST.NAME).from(TEST).where(TEST.ID.eq());

  @Test
  void getAllUpdateWithKeys() {
    PureDBC<Iterable<Tuple2<Integer, String>>> program = For.with(PureDBC.monad())
        .andThen(() -> update(createTable).kind1())
        .andThen(() -> update(deleteAll).kind1())
        .andThen(() -> updateWithKeys(insertRowWithKey.bind("toni"), row -> row.getInteger(TEST.ID)).kind1())
        .andThen(() -> updateWithKeys(insertRowWithKey.bind("pepe"), row -> row.getInteger(TEST.ID)).kind1())
        .andThen(() -> queryIterable(findAll, TEST::asTuple).kind1())
        .fix(PureDBC::narrowK);

    ImmutableList<Tuple2<Integer, String>> expected = listOf(Tuple.of(1, "toni"), Tuple.of(2, "pepe"));

    assertEquals(expected, program.unsafeRun(dataSource));
  }

  @Test
  void queryAll() {
    PureDBC<Iterable<Tuple2<Integer, String>>> program =
        update(createTable)
            .andThen(update(deleteAll))
            .andThen(update(insertRow.bind(1, "toni")))
            .andThen(update(insertRow.bind(2, "pepe")))
            .andThen(queryIterable(findAll, TEST::asTuple));

    assertProgram(program, listOf(Tuple.of(1, "toni"), Tuple.of(2, "pepe")));
  }

  @Test
  void queryIn() {
    PureDBC<Iterable<Tuple2<Integer, String>>> program =
        update(createTable)
            .andThen(update(deleteAll))
            .andThen(update(insertRow.bind(1, "toni")))
            .andThen(update(insertRow.bind(2, "pepe")))
            .andThen(queryIterable(findIn.bind(arrayOf(1, 2, 3)), TEST::asTuple));

    assertProgram(program, listOf(Tuple.of(1, "toni"), Tuple.of(2, "pepe")));
  }

  @Test
  void queryBetween() {
    PureDBC<Iterable<Tuple2<Integer, String>>> program =
        update(createTable)
            .andThen(update(deleteAll))
            .andThen(update(insertRow.bind(1, "toni")))
            .andThen(update(insertRow.bind(2, "pepe")))
            .andThen(queryIterable(findBetween.bind(Range.of(1, 2)), TEST::asTuple));

    assertProgram(program, listOf(Tuple.of(1, "toni"), Tuple.of(2, "pepe")));
  }

  @Test
  void count() {
    PureDBC<Integer> program =
      update(createTable)
        .andThen(update(deleteAll))
        .andThen(update(insertRow.bind(1, "toni")))
        .andThen(update(insertRow.bind(2, "pepe")))
        .andThen(update(insertRow.bind(3, "paco")))
        .andThen(PureDBC.query(count, rs -> rs.next() ? rs.getInt("elements") : 0));

    assertProgram(program, 3);
  }

  @Test
  void queryJustOne() {
    PureDBC<Option<Tuple2<Integer, String>>> program =
        update(createTable)
            .andThen(update(deleteOne.bind(1)))
            .andThen(update(insertRow.bind(1, "toni")))
            .andThen(update(updateRow.bind("pepe", 1)))
            .andThen(queryOne(findOne.bind(1), TEST::asTuple));

    assertProgram(program, Option.some(Tuple.of(1, "pepe")));
  }

  @Test
  void queryMetaData() {
    PureDBC<Integer> program =
        update(createTable).andThen(PureDBC.query(findAll, rs -> rs.getMetaData().getColumnCount()));

    assertEquals(2, program.unsafeRun(dataSource()));
  }

  @Test
  void queryError() {
    PureDBC<Option<Tuple2<Integer, String>>> program =
        update(dropTable)
            .andThen(update(deleteAll))
            .andThen(update(insertRow.bind(1, "toni")))
            .andThen(queryOne(findOne.bind(1), TEST::asTuple));

    assertProgramFailure(program);
  }

  private DataSource dataSource() {
    HikariConfig poolConfig = new HikariConfig();
    poolConfig.setJdbcUrl("jdbc:h2:mem:test");
    poolConfig.setUsername("sa");
    poolConfig.setPassword("");
    return new HikariDataSource(poolConfig);
  }

  private <T> void assertProgram(PureDBC<T> program, T expected) {
    assertAll(
        () -> assertEquals(expected, program.unsafeRun(dataSource)),
        () -> assertEquals(Try.success(expected), program.safeRun(dataSource)),
        () -> assertEquals(expected, program.unsafeRunIO(dataSource).unsafeRunSync()),
        () -> assertEquals(Try.success(expected), program.safeRunIO(dataSource).safeRunSync()),
        () -> assertEquals(Try.success(expected), program.asyncRun(dataSource).await())
    );
  }

  private void assertProgramFailure(PureDBC<Option<Tuple2<Integer, String>>> program) {
    assertAll(
        () -> assertThrows(SQLException.class, () -> program.unsafeRun(dataSource)),
        () -> assertTrue(program.safeRun(dataSource).isFailure()),
        () -> assertThrows(SQLException.class, () -> program.unsafeRunIO(dataSource).unsafeRunSync()),
        () -> assertTrue(program.safeRunIO(dataSource).safeRunSync().isFailure()),
        () -> assertTrue(program.asyncRun(dataSource).await().isFailure())
    );
  }
}

final class TestTable implements Table2<Integer, String> {

  public final Field<Integer> ID;
  public final Field<String> NAME;

  private final String name;

  TestTable() {
    this.name = "test";
    this.ID = Field.of("id");
    this.NAME = Field.of("name");
  }

  private TestTable(TestTable other, String alias) {
    this.name = "test as " + alias;
    this.ID = other.ID.alias(alias);
    this.NAME = other.NAME.alias(alias);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public NonEmptyList<Field<?>> all() {
    return NonEmptyList.of(ID, NAME);
  }

  public TestTable as(String alias) {
    return new TestTable(this, requireNonNull(alias));
  }

  public Tuple2<Integer, String> asTuple(Row row) throws SQLException {
    return Tuple.of(row.getInteger(ID), row.getString(NAME));
  }
}

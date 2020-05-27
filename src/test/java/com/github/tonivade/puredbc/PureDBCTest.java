/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.puredbc.sql.SQL;
import com.github.tonivade.puredbc.sql.SQL1;
import com.github.tonivade.puredbc.sql.SQL2;
import com.github.tonivade.puredbc.sql.Table2;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.NonEmptyList;
import com.github.tonivade.purefun.data.Range;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.purefun.type.Validation;
import com.github.tonivade.purefun.typeclasses.For;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.R2dbcBadGrammarException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

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
import static com.github.tonivade.purefun.Unit.unit;
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
  private final ConnectionFactory connectionFactory = connectionFactory();

  private final SQL createTable = sql(
      "create table if not exists test(",
        "id identity primary key,",
        "name varchar(100)",
      ")");
  private final SQL dropTable = sql("drop table if exists test");
  private final SQL deleteAll = delete(TEST);
  private final SQL1<Long> deleteOne = delete(TEST).where(TEST.ID.eq());
  private final SQL1<String> insertRowWithKey = insert(TEST).values(TEST.NAME);
  private final SQL2<Long, String> insertRow = insert(TEST).values(TEST.ID, TEST.NAME);
  private final SQL2<String, Long> updateRow = update(TEST).set(TEST.NAME).where(TEST.ID.eq());
  private final SQL findAll = select(ALIAS.all()).from(ALIAS);
  private final SQL1<Iterable<Long>> findIn = select(ALIAS.all()).from(ALIAS).where(ALIAS.ID.in());
  private final SQL1<Range> findBetween = select(ALIAS.all()).from(ALIAS).where(between(ALIAS.ID));
  private final SQL count = select(TEST.ID.count().as("elements")).from(TEST);
  private final SQL1<Long> findOne = select(TEST.ID, TEST.NAME).from(TEST).where(TEST.ID.eq());

  @Test
  void getAllUpdateWithKeys() {
    PureDBC<Iterable<Tuple2<Long, String>>> program = For.with(PureDBC.monad())
        .andThen(() -> update(dropTable))
        .andThen(() -> update(createTable))
        .andThen(() -> updateWithKeys(insertRowWithKey.bind("toni"), TEST.ID))
        .andThen(() -> updateWithKeys(insertRowWithKey.bind("pepe"), TEST.ID))
        .andThen(() -> queryIterable(findAll, TEST::asTuple))
        .fix(PureDBCOf::narrowK);

    assertProgram(program, listOf(Tuple.of(1L, "toni"), Tuple.of(2L, "pepe")));
  }

  @Test
  void queryAll() {
    PureDBC<Iterable<Tuple2<Long, String>>> program =
        prepareTable()
            .andThen(update(insertRow.bind(1L, "toni")))
            .andThen(update(insertRow.bind(2L, "pepe")))
            .andThen(queryIterable(findAll, TEST::asTuple));

    assertProgram(program, listOf(Tuple.of(1L, "toni"), Tuple.of(2L, "pepe")));
  }

  @Test
  void queryIn() {
    PureDBC<Iterable<Tuple2<Long, String>>> program =
        prepareTable()
            .andThen(update(insertRow.bind(1L, "toni")))
            .andThen(update(insertRow.bind(2L, "pepe")))
            .andThen(queryIterable(findIn.bind(arrayOf(1L, 2L, 3L)), TEST::asTuple));

    assertProgram(program, listOf(Tuple.of(1L, "toni"), Tuple.of(2L, "pepe")));
  }

  @Test
  void queryBetween() {
    PureDBC<Iterable<Tuple2<Long, String>>> program =
        prepareTable()
            .andThen(update(insertRow.bind(1L, "toni")))
            .andThen(update(insertRow.bind(2L, "pepe")))
            .andThen(queryIterable(findBetween.bind(Range.of(1, 2)), TEST::asTuple));

    assertProgram(program, listOf(Tuple.of(1L, "toni"), Tuple.of(2L, "pepe")));
  }

  @Test
  void count() {
    PureDBC<Option<Long>> program =
      prepareTable()
        .andThen(update(insertRow.bind(1L, "toni")))
        .andThen(update(insertRow.bind(2L, "pepe")))
        .andThen(update(insertRow.bind(3L, "paco")))
        .andThen(PureDBC.queryOne(count, row -> row.getLong(Field.of("elements"))));

    assertProgram(program, Option.some(3L));
  }

  @Test
  void queryJustOne() {
    PureDBC<Option<Tuple2<Long, String>>> program =
        update(createTable)
            .andThen(update(deleteOne.bind(1L)))
            .andThen(update(insertRow.bind(1L, "toni")))
            .andThen(update(updateRow.bind("pepe", 1L)))
            .andThen(queryOne(findOne.bind(1L), TEST::asTuple));

    assertProgram(program, Option.some(Tuple.of(1L, "pepe")));
  }

  @Test
  void queryMetaData() {
    PureDBC<Option<Integer>> program =
        prepareTable()
            .andThen(update(insertRow.bind(1L, "toni")))
            .andThen(PureDBC.queryMeta(findAll, RowMetaData::columnCount));

    assertProgram(program, Option.some(2));
  }

  @Test
  void queryValidate() {
    PureDBC<Option<Validation<Iterable<String>, Unit>>> program =
        prepareTable()
            .andThen(update(insertRow.bind(1L, "toni")))
            .andThen(PureDBC.queryMeta(findAll.limit(1), TEST::validate));

    assertProgram(program, Option.some(Validation.valid(unit())));
  }

  @Test
  void queryError() {
    PureDBC<Option<Tuple2<Long, String>>> program =
        update(dropTable)
            .andThen(update(deleteAll))
            .andThen(update(insertRow.bind(1L, "toni")))
            .andThen(queryOne(findOne.bind(1L), TEST::asTuple));

    assertProgramFailure(program);
  }

  private DataSource dataSource() {
    HikariConfig poolConfig = new HikariConfig();
    poolConfig.setJdbcUrl("jdbc:h2:mem:test");
    poolConfig.setUsername("sa");
    poolConfig.setPassword("");
    return new HikariDataSource(poolConfig);
  }

  private PureDBC<Unit> prepareTable() {
    return update(createTable)
        .andThen(update(deleteAll));
  }

  private ConnectionFactory connectionFactory() {
    ConnectionFactoryOptions baseOptions = ConnectionFactoryOptions.parse("r2dbc:h2:mem:///test");
    ConnectionFactoryOptions.Builder builder = ConnectionFactoryOptions.builder().from(baseOptions)
        .option(ConnectionFactoryOptions.USER, "sa")
        .option(ConnectionFactoryOptions.PASSWORD, "");
    return ConnectionFactories.get(builder.build());
  }

  private <T> void assertProgram(PureDBC<T> program, T expected) {
    assertAll(
        () -> assertEquals(expected, program.unsafeRun(dataSource)),
        () -> assertEquals(Try.success(expected), program.safeRun(dataSource)),
        () -> assertEquals(expected, program.unsafeRunIO(dataSource).unsafeRunSync()),
        () -> assertEquals(Try.success(expected), program.safeRunIO(dataSource).safeRunSync()),
        () -> assertEquals(Try.success(expected), program.asyncRun(dataSource).await()),
        () -> assertEquals(expected, Mono.from(program.reactorRun(connectionFactory)).block())
    );
  }

  private void assertProgramFailure(PureDBC<Option<Tuple2<Long, String>>> program) {
    assertAll(
        () -> assertThrows(SQLException.class, () -> program.unsafeRun(dataSource)),
        () -> assertTrue(program.safeRun(dataSource).isFailure()),
        () -> assertThrows(SQLException.class, () -> program.unsafeRunIO(dataSource).unsafeRunSync()),
        () -> assertTrue(program.safeRunIO(dataSource).safeRunSync().isFailure()),
        () -> assertTrue(program.asyncRun(dataSource).await().isFailure()),
        () -> assertThrows(R2dbcBadGrammarException.class,
            () -> Mono.from(program.reactorRun(connectionFactory)).block())
    );
  }
}

final class TestTable implements Table2<Long, String> {

  public final Field<Long> ID;
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

  public Tuple2<Long, String> asTuple(Row row) {
    return Tuple2.of(row.getLong(ID), row.getString(NAME));
  }
}

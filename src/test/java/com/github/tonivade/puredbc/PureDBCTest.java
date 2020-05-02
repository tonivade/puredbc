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
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.NonEmptyList;
import com.github.tonivade.purefun.data.Range;
import com.github.tonivade.purefun.instances.TryInstances;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.purefun.typeclasses.For;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.sql.ResultSet;
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
  private final ConnectionFactory connectionFactory = connectionFactory();

  private final SQL createTable = sql(
      "create table if not exists test(",
      "id identity primary key,",
      "name varchar(100))");
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
    PureDBC<Iterable<Try<Tuple2<Long, String>>>> program = For.with(PureDBC.monad())
        .andThen(() -> update(createTable).kind1())
        .andThen(() -> update(deleteAll).kind1())
        .andThen(() -> updateWithKeys(insertRowWithKey.bind("toni"), row -> row.getLong(TEST.ID)).kind1())
        .andThen(() -> updateWithKeys(insertRowWithKey.bind("pepe"), row -> row.getLong(TEST.ID)).kind1())
        .andThen(() -> queryIterable(findAll, TEST::asTuple).kind1())
        .fix(PureDBC::narrowK);

    ImmutableList<Try<Tuple2<Long, String>>> expected =
        listOf(Try.success(Tuple.of(1l, "toni")), Try.success(Tuple.of(2l, "pepe")));

    assertEquals(expected, program.unsafeRun(dataSource));
  }

  @Test
  void queryAll() {
    PureDBC<Iterable<Try<Tuple2<Long, String>>>> program =
        update(createTable)
            .andThen(update(deleteAll))
            .andThen(update(insertRow.bind(1l, "toni")))
            .andThen(update(insertRow.bind(2l, "pepe")))
            .andThen(queryIterable(findAll, TEST::asTuple));

    assertProgram(program, listOf(Try.success(Tuple.of(1l, "toni")), Try.success(Tuple.of(2l, "pepe"))));
  }

  @Test
  void queryIn() {
    PureDBC<Iterable<Try<Tuple2<Long, String>>>> program =
        update(createTable)
            .andThen(update(deleteAll))
            .andThen(update(insertRow.bind(1l, "toni")))
            .andThen(update(insertRow.bind(2l, "pepe")))
            .andThen(queryIterable(findIn.bind(arrayOf(1l, 2l, 3l)), TEST::asTuple));

    assertProgram(program, listOf(Try.success(Tuple.of(1l, "toni")), Try.success(Tuple.of(2l, "pepe"))));
  }

  @Test
  void queryBetween() {
    PureDBC<Iterable<Try<Tuple2<Long, String>>>> program =
        update(createTable)
            .andThen(update(deleteAll))
            .andThen(update(insertRow.bind(1l, "toni")))
            .andThen(update(insertRow.bind(2l, "pepe")))
            .andThen(queryIterable(findBetween.bind(Range.of(1, 2)), TEST::asTuple));

    assertProgram(program, listOf(Try.success(Tuple.of(1l, "toni")), Try.success(Tuple.of(2l, "pepe"))));
  }

  @Test
  void count() {
    PureDBC<Integer> program =
      update(createTable)
        .andThen(update(deleteAll))
        .andThen(update(insertRow.bind(1l, "toni")))
        .andThen(update(insertRow.bind(2l, "pepe")))
        .andThen(update(insertRow.bind(3l, "paco")))
        .andThen(PureDBC.query(count, result -> {
          ResultSet rs = result.unwrap();
          return rs.next() ? rs.getInt("elements") : 0;
        }));

    assertProgram(program, 3);
  }

  @Test
  void queryJustOne() {
    PureDBC<Option<Try<Tuple2<Long, String>>>> program =
        update(createTable)
            .andThen(update(deleteOne.bind(1l)))
            .andThen(update(insertRow.bind(1l, "toni")))
            .andThen(update(updateRow.bind("pepe", 1l)))
            .andThen(queryOne(findOne.bind(1l), TEST::asTuple));

    assertProgram(program, Option.some(Try.success(Tuple.of(1l, "pepe"))));
  }

  @Test
  void queryJustOneReactor() {
    PureDBC<Option<Try<Tuple2<Long, String>>>> program =
        update(createTable)
            .andThen(update(deleteOne.bind(1l)))
            .andThen(update(insertRow.bind(1l, "toni")))
            .andThen(update(updateRow.bind("pepe", 1l)))
            .andThen(queryOne(findOne.bind(1l), TEST::asTuple));

    Option<Try<Tuple2<Long, String>>> result = Mono.from(program.reactRun(connectionFactory)).block();
    assertEquals(Option.some(Try.success(Tuple.of(1l, "pepe"))), result);
  }

  @Test
  void queryMetaData() {
    PureDBC<Integer> program =
        update(createTable).andThen(PureDBC.query(findAll, rs -> rs.<ResultSet>unwrap().getMetaData().getColumnCount()));

    assertEquals(2, program.unsafeRun(dataSource()));
  }

  @Test
  void queryError() {
    PureDBC<Option<Try<Tuple2<Long, String>>>> program =
        update(dropTable)
            .andThen(update(deleteAll))
            .andThen(update(insertRow.bind(1l, "toni")))
            .andThen(queryOne(findOne.bind(1l), TEST::asTuple));

    assertProgramFailure(program);
  }

  private DataSource dataSource() {
    HikariConfig poolConfig = new HikariConfig();
    poolConfig.setJdbcUrl("jdbc:h2:mem:test");
    poolConfig.setUsername("sa");
    poolConfig.setPassword("");
    return new HikariDataSource(poolConfig);
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
        () -> assertEquals(Try.success(expected), program.asyncRun(dataSource).await())
    );
  }

  private void assertProgramFailure(PureDBC<Option<Try<Tuple2<Long, String>>>> program) {
    assertAll(
        () -> assertThrows(SQLException.class, () -> program.unsafeRun(dataSource)),
        () -> assertTrue(program.safeRun(dataSource).isFailure()),
        () -> assertThrows(SQLException.class, () -> program.unsafeRunIO(dataSource).unsafeRunSync()),
        () -> assertTrue(program.safeRunIO(dataSource).safeRunSync().isFailure()),
        () -> assertTrue(program.asyncRun(dataSource).await().isFailure())
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

  public Try<Tuple2<Long, String>> asTuple(Row row) {
    return TryInstances.applicative().map2(row.getLong(ID), row.getString(NAME), Tuple2::of).fix1(Try::narrowK);
  }
}

/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.Precondition.checkNonNull;
import static com.github.tonivade.purefun.Unit.unit;
import java.util.List;
import org.reactivestreams.Publisher;
import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.Range;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.type.Option;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class R2dbcTemplate {

  private final ConnectionFactory connectionFactory;

  public R2dbcTemplate(ConnectionFactory connectionFactory) {
    this.connectionFactory = checkNonNull(connectionFactory);
  }

  public Mono<Unit> update(String query, Sequence<?> params) {
    return Mono.from(connectionFactory.create())
        .flatMap(conn -> doUpdate(query, params, conn)
            .delayUntil(result -> conn.commitTransaction())
            .doFinally(stmt -> conn.close()))
        .thenReturn(unit());
  }

  public <T> Mono<Option<T>> updateWithKeys(String query, Sequence<?> params, Field<T> field) {
    return Mono.from(connectionFactory.create())
        .flatMap(conn -> doUpdateWithKeys(query, params, conn, field)
            .flatMap(result -> Mono.from(applyToRow(row -> row.get(field), result)))
            .delayUntil(result -> conn.commitTransaction())
            .doFinally(stmt -> conn.close()))
        .map(Option::some).defaultIfEmpty(Option.none());
  }

  public <T> Mono<Option<T>> queryMeta(String query, Sequence<?> params, Function1<RowMetaData, T> rowMapper) {
    return doQuery(query, params)
        .flatMap(result -> Mono.from(applyToMeta(rowMapper, result)))
        .map(Option::some).defaultIfEmpty(Option.none());
  }

  public <T> Mono<Option<T>> queryOne(String query, Sequence<?> params, Function1<Row, T> rowMapper) {
    return doQuery(query, params)
        .flatMap(result -> Mono.from(applyToRow(rowMapper, result)))
        .map(Option::some).defaultIfEmpty(Option.none());
  }

  public <T> Flux<List<T>> queryIterable(String query, Sequence<?> params, Function1<Row, T> rowMapper) {
    return doQuery(query, params)
        .flatMapMany(result -> Flux.from(applyToRow(rowMapper, result))).buffer(10);
  }

  private Mono<io.r2dbc.spi.Result> doUpdate(String query, Sequence<?> params, Connection conn) {
    return Mono.from(conn.beginTransaction())
        .then(createStatement(query, params, conn)
        .flatMap(stmt -> Mono.from(stmt.execute())));
  }

  private Mono<io.r2dbc.spi.Result> doUpdateWithKeys(String query, Sequence<?> params, Connection conn, Field<?> field) {
    return Mono.from(conn.beginTransaction())
        .then(createStatement(query, params, conn).map(stmt -> stmt.returnGeneratedValues(field.name()))
        .flatMap(stmt -> Mono.from(stmt.execute())));
  }

  private Mono<io.r2dbc.spi.Result> doQuery(String query, Sequence<?> params) {
    return Mono.from(connectionFactory.create())
        .flatMap(conn -> createStatement(query, params, conn)
        .flatMap(stmt -> Mono.from(stmt.execute()))
        .doFinally(stmt -> Mono.from(conn.close()).then(Mono.empty())));
  }

  private Mono<io.r2dbc.spi.Statement> createStatement(String query, Sequence<?> params, Connection conn) {
    return Mono.just(conn.createStatement(query))
        .map(stmt -> {
          int i = 0;
          for (Object param : params) {
            if (param instanceof Range range) {
              stmt.bind(i++, range.begin());
              stmt.bind(i++, range.end());
            } else if (param instanceof Iterable<?> iterable) {
              for (Object p : iterable) {
                stmt.bind(i++, p);
              }
            } else {
              stmt.bind(i++, param);
            }
          }
          return stmt;
        });
  }

  private <T> Publisher<T> applyToRow(Function1<Row, T> rowMapper, io.r2dbc.spi.Result result) {
    return result.map((row, meta) -> rowMapper.compose(R2dbcRow::new).apply(row));
  }

  private <T> Publisher<T> applyToMeta(Function1<RowMetaData, T> rowMapper, io.r2dbc.spi.Result result) {
    return result.map((row, meta) -> rowMapper.compose(R2dbcRowMetaData::new).apply(meta));
  }
}

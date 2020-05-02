/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.Range;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.type.Option;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.tonivade.purefun.Unit.unit;
import static java.util.Objects.requireNonNull;

public final class R2dbcTemplate {

  private final ConnectionFactory connectionFactory;

  public R2dbcTemplate(ConnectionFactory connectionFactory) {
    this.connectionFactory = requireNonNull(connectionFactory);
  }

  public Mono<Unit> update(String query, Sequence<?> params) {
    return Mono.from(connectionFactory.create())
        .flatMap(conn -> _update(query, params, conn)
            .delayUntil(result -> conn.commitTransaction())
            .doFinally(stmt -> conn.close()))
        .thenReturn(unit());
  }

  public <T> Mono<Option<T>> updateWithKeys(String query, Sequence<?> params, Function1<Row, T> rowMapper) {
    return Mono.from(connectionFactory.create())
        .flatMap(conn -> _update(query, params, conn)
            .flatMap(result -> Mono.from(apply(rowMapper, result)))
            .delayUntil(result -> conn.commitTransaction())
            .doFinally(stmt -> conn.close()))
        .map(Option::some).defaultIfEmpty(Option.none());
  }

  public <T> Flux<Option<T>> query(String query, Sequence<?> params, Function1<Result, T> mapper) {
    // TODO:
    return Flux.empty();
  }

  public <T> Mono<Option<T>> queryOne(String query, Sequence<?> params, Function1<Row, T> rowMapper) {
    return _query(query, params)
        .flatMap(result -> Mono.from(apply(rowMapper, result)))
        .map(Option::some).defaultIfEmpty(Option.none());
  }

  public <T> Flux<List<T>> queryIterable(String query, Sequence<?> params, Function1<Row, T> rowMapper) {
    return _query(query, params)
        .flatMapMany(result -> Flux.from(apply(rowMapper, result))).buffer(10);
  }

  private Mono<io.r2dbc.spi.Result> _update(String query, Sequence<?> params, Connection conn) {
    return Mono.from(conn.beginTransaction()).then(createStatement(query, params, conn));
  }

  private Mono<io.r2dbc.spi.Result> _query(String query, Sequence<?> params) {
    return Mono.from(connectionFactory.create())
        .flatMap(conn -> createStatement(query, params, conn)
            .doFinally(stmt -> Mono.from(conn.close()).then(Mono.empty())));
  }

  private Mono<io.r2dbc.spi.Result> createStatement(String query, Sequence<?> params, Connection conn) {
    return Mono.just(conn.createStatement(query))
        .flatMap(stmt -> {
          int i = 0;
          for (Object param : params) {
            if (param instanceof Range) {
              Range range = (Range) param;
              stmt.bind(i++, range.begin());
              stmt.bind(i++, range.end());
            } else if (param instanceof Iterable) {
              for (Object p : (Iterable<?>) param) {
                stmt.bind(i++, p);
              }
            } else {
              stmt.bind(i++, param);
            }
          }
          return Mono.from(stmt.execute());
        });
  }

  private <T> Publisher<T> apply(Function1<Row, T> rowMapper, io.r2dbc.spi.Result result) {
    return result.map((row, meta) -> rowMapper.compose(R2dbcRow::new).apply(row));
  }
}

/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.type.Option;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;

public final class R2dbcTemplate {

  private final ConnectionFactory connectionFactory;

  public R2dbcTemplate(ConnectionFactory connectionFactory) {
    this.connectionFactory = requireNonNull(connectionFactory);
  }

  public Mono<Unit> update(String query, Sequence<?> params) {
    // TODO:
    return Mono.empty();
  }

  public <T> Mono<Option<T>> updateWithKeys(String query, Sequence<?> params, Function1<Row, T> rowMapper) {
    // TODO:
    return Mono.empty();
  }

  public <T> Flux<Option<T>> query(String query, Sequence<?> params, Function1<Result, T> mapper) {
    // TODO:
    return Flux.empty();
  }

  public <T> Mono<Option<T>> queryOne(String query, Sequence<?> params, Function1<Row, T> mapper) {
    return Mono.from(connectionFactory.create())
        .flatMap(conn ->
            Mono.just(conn.createStatement(query))
                .flatMap(stmt -> {
                  int i = 1;
                  for (Object param : params) {
                    stmt.bind(i++, param);
                  }
                  return Mono.from(stmt.execute());
                }).doFinally(stmt -> Mono.from(conn.close()).then(Mono.empty())))
        .flatMap(result -> Mono.from(result.map((row, meta) -> mapper.compose(R2dbcRow::new).apply(row))))
        .map(Option::some).defaultIfEmpty(Option.none());
  }

  public <T> Flux<T> queryIterable(String query, Sequence<?> params, Function1<Row, T> mapper) {
    return Mono.from(connectionFactory.create())
        .flatMap(conn ->
            Mono.just(conn.createStatement(query))
                .flatMap(stmt -> {
                  int i = 1;
                  for (Object param : params) {
                    stmt.bind(i++, param);
                  }
                  return Mono.from(stmt.execute());
                }).doFinally(stmt -> Flux.from(conn.close()).then(Mono.empty()))
        ).flatMapMany(result -> Flux.from(result.map((row, meta) -> mapper.compose(R2dbcRow::new).apply(row))));
  }
}

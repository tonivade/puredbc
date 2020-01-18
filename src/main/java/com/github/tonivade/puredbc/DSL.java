/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.puredbc.sql.Row;
import com.github.tonivade.puredbc.sql.SQL;
import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Higher1;
import com.github.tonivade.purefun.HigherKind;
import com.github.tonivade.purefun.Kind;
import com.github.tonivade.purefun.Sealed;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.type.Option;

import java.sql.ResultSet;

import static com.github.tonivade.purefun.Function1.cons;
import static com.github.tonivade.purefun.Unit.unit;
import static java.util.Objects.requireNonNull;

@Sealed
@HigherKind
interface DSL<T> {

  <F extends Kind> Higher1<F, T> accept(Visitor<F> visitor);

  interface Visitor<F extends Kind> {
    Higher1<F, Unit> visit(DSL.Update update);
    <T> Higher1<F, Option<T>> visit(DSL.UpdateWithKeys<T> update);
    <T> Higher1<F, T> visit(Query<T> query);
    <T> Higher1<F, Iterable<T>> visit(QueryIterable<T> query);
    <T> Higher1<F, Option<T>> visit(DSL.QueryOne<T> query);
  }

  abstract class AbstractQuery<T> {

    private final SQL query;
    private final Function1<ResultSet, T> extractor;

    private AbstractQuery(SQL query, Function1<ResultSet, T> extractor) {
      this.query = requireNonNull(query);
      this.extractor = requireNonNull(extractor);
    }

    public String getQuery() { return query.getQuery(); }

    public Sequence<?> getParams() { return query.getParams(); }

    public Function1<ResultSet, T> getExtractor() { return extractor; }

    @Override
    public String toString() { return query.toString(); }
  }

  final class Query<T> extends AbstractQuery<T> implements DSL<T> {

    protected Query(SQL query, Function1<ResultSet, T> extractor) {
      super(query, extractor);
    }

    @Override
    public <F extends Kind> Higher1<F, T> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    @Override
    public String toString() {
      return String.format("Query{query=%s}", super.toString());
    }
  }

  final class QueryIterable<T> extends AbstractQuery<T> implements DSL<Iterable<T>> {

    protected QueryIterable(SQL query, Function1<Row, T> rowMapper) {
      super(query, rowMapper.compose(Row::new));
    }

    @Override
    public <F extends Kind> Higher1<F, Iterable<T>> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    @Override
    public String toString() {
      return String.format("QueryIterable{query=%s}", super.toString());
    }
  }

  final class QueryOne<T> extends AbstractQuery<T> implements DSL<Option<T>> {

    protected QueryOne(SQL query, Function1<Row, T> rowMapper) {
      super(query, rowMapper.compose(Row::new));
    }

    @Override
    public <F extends Kind> Higher1<F, Option<T>> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    @Override
    public String toString() {
      return String.format("QueryOne{query=%s}", super.toString());
    }
  }

  final class UpdateWithKeys<T> extends AbstractQuery<T> implements DSL<Option<T>> {

    protected UpdateWithKeys(SQL query, Function1<Row, T> extractor) {
      super(query, extractor.compose(Row::new));
    }

    @Override
    public <F extends Kind> Higher1<F, Option<T>> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    @Override
    public String toString() {
      return String.format("UpdateWithKeys{query=%s}", super.toString());
    }
  }

  final class Update extends AbstractQuery<Unit> implements DSL<Unit> {

    protected Update(SQL query) {
      super(query, cons(unit()));
    }

    @Override
    public <F extends Kind> Higher1<F, Unit> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    @Override
    public String toString() {
      return String.format("Update{query=%s}", super.toString());
    }
  }
}

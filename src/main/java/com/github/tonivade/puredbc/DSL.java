/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static java.util.Objects.requireNonNull;
import com.github.tonivade.puredbc.sql.SQL;
import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Higher1;
import com.github.tonivade.purefun.HigherKind;
import com.github.tonivade.purefun.Kind;
import com.github.tonivade.purefun.Sealed;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.type.Option;

@Sealed
@HigherKind
interface DSL<T> extends DSLOf<T> {

  <F extends Kind> Higher1<F, T> accept(Visitor<F> visitor);

  interface Visitor<F extends Kind> {

    Higher1<F, Unit> visit(DSL.Update update);

    <T> Higher1<F, Option<T>> visit(DSL.UpdateWithKeys<T> update);

    <T> Higher1<F, Option<T>> visit(DSL.QueryMeta<T> query);

    <T> Higher1<F, Iterable<T>> visit(QueryIterable<T> query);

    <T> Higher1<F, Option<T>> visit(DSL.QueryOne<T> query);
  }

  abstract class AbstractQuery {

    private final SQL query;

    private AbstractQuery(SQL query) {
      this.query = requireNonNull(query);
    }

    public String getQuery() { return query.getQuery(); }

    public Sequence<?> getParams() { return query.getParams(); }

    @Override
    public String toString() { return query.toString(); }
  }

  final class QueryIterable<T> extends AbstractQuery implements DSL<Iterable<T>> {

    private final Function1<Row, T> rowMapper;

    protected QueryIterable(SQL query, Function1<Row, T> rowMapper) {
      super(query);
      this.rowMapper = requireNonNull(rowMapper);
    }

    public Function1<Row, T> getRowMapper() {
      return rowMapper;
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

  final class QueryMeta<T> extends AbstractQuery implements DSL<Option<T>> {

    private final Function1<RowMetaData, T> rowMapper;

    protected QueryMeta(SQL query, Function1<RowMetaData, T> rowMapper) {
      super(query);
      this.rowMapper = requireNonNull(rowMapper);
    }

    public Function1<RowMetaData, T> getRowMapper() {
      return rowMapper;
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

  final class QueryOne<T> extends AbstractQuery implements DSL<Option<T>> {

    private final Function1<Row, T> rowMapper;

    protected QueryOne(SQL query, Function1<Row, T> rowMapper) {
      super(query);
      this.rowMapper = requireNonNull(rowMapper);
    }

    public Function1<Row, T> getRowMapper() {
      return rowMapper;
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

  final class UpdateWithKeys<T> extends AbstractQuery implements DSL<Option<T>> {

    private final Function1<Row, T> rowMapper;

    protected UpdateWithKeys(SQL query, Function1<Row, T> rowMapper) {
      super(query);
      this.rowMapper = requireNonNull(rowMapper);
    }

    public Function1<Row, T> getRowMapper() {
      return rowMapper;
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

  final class Update extends AbstractQuery implements DSL<Unit> {

    protected Update(SQL query) {
      super(query);
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

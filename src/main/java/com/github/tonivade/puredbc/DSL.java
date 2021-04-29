/*
 * Copyright (c) 2020-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.Precondition.checkNonNull;
import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.puredbc.sql.SQL;
import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.HigherKind;
import com.github.tonivade.purefun.Kind;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.Witness;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.type.Option;

@HigherKind(sealed = true)
interface DSL<T> extends DSLOf<T> {

  <F extends Witness> Kind<F, T> accept(Visitor<F> visitor);

  interface Visitor<F extends Witness> {

    Kind<F, Unit> visit(DSL.Update update);

    <T> Kind<F, Option<T>> visit(DSL.UpdateWithKeys<T> update);

    <T> Kind<F, Option<T>> visit(DSL.QueryMeta<T> query);

    <T> Kind<F, Iterable<T>> visit(QueryIterable<T> query);

    <T> Kind<F, Option<T>> visit(DSL.QueryOne<T> query);
  }

  abstract class AbstractQuery {

    private final SQL query;

    private AbstractQuery(SQL query) {
      this.query = checkNonNull(query);
    }

    public String getQuery() { return query.getQuery(); }

    public Sequence<?> getParams() { return query.getParams(); }

    @Override
    public String toString() { return query.toString(); }
  }

  final class QueryIterable<T> extends AbstractQuery implements SealedDSL<Iterable<T>> {

    private final Function1<Row, T> rowMapper;

    protected QueryIterable(SQL query, Function1<Row, T> rowMapper) {
      super(query);
      this.rowMapper = checkNonNull(rowMapper);
    }

    public Function1<Row, T> getRowMapper() {
      return rowMapper;
    }

    @Override
    public <F extends Witness> Kind<F, Iterable<T>> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    @Override
    public String toString() {
      return String.format("QueryIterable{query=%s}", super.toString());
    }
  }

  final class QueryMeta<T> extends AbstractQuery implements SealedDSL<Option<T>> {

    private final Function1<RowMetaData, T> rowMapper;

    protected QueryMeta(SQL query, Function1<RowMetaData, T> rowMapper) {
      super(query);
      this.rowMapper = checkNonNull(rowMapper);
    }

    public Function1<RowMetaData, T> getRowMapper() {
      return rowMapper;
    }

    @Override
    public <F extends Witness> Kind<F, Option<T>> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    @Override
    public String toString() {
      return String.format("QueryOne{query=%s}", super.toString());
    }
  }

  final class QueryOne<T> extends AbstractQuery implements SealedDSL<Option<T>> {

    private final Function1<Row, T> rowMapper;

    protected QueryOne(SQL query, Function1<Row, T> rowMapper) {
      super(query);
      this.rowMapper = checkNonNull(rowMapper);
    }

    public Function1<Row, T> getRowMapper() {
      return rowMapper;
    }

    @Override
    public <F extends Witness> Kind<F, Option<T>> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    @Override
    public String toString() {
      return String.format("QueryOne{query=%s}", super.toString());
    }
  }

  final class UpdateWithKeys<T> extends AbstractQuery implements SealedDSL<Option<T>> {

    private final Field<T> field;

    protected UpdateWithKeys(SQL query, Field<T> field) {
      super(query);
      this.field = checkNonNull(field);
    }

    public Field<T> getField() {
      return field;
    }

    @Override
    public <F extends Witness> Kind<F, Option<T>> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    @Override
    public String toString() {
      return String.format("UpdateWithKeys{query=%s}", super.toString());
    }
  }

  final class Update extends AbstractQuery implements SealedDSL<Unit> {

    protected Update(SQL query) {
      super(query);
    }

    @Override
    public <F extends Witness> Kind<F, Unit> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    @Override
    public String toString() {
      return String.format("Update{query=%s}", super.toString());
    }
  }
}

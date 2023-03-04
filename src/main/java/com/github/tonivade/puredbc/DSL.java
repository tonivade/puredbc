/*
 * Copyright (c) 2020-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
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
import com.github.tonivade.purefun.type.Option;

@HigherKind
sealed interface DSL<T> extends DSLOf<T> {

  <F extends Witness> Kind<F, T> accept(Visitor<F> visitor);

  interface Visitor<F extends Witness> {

    Kind<F, Unit> visit(DSL.Update update);

    <T> Kind<F, Option<T>> visit(DSL.UpdateWithKeys<T> update);

    <T> Kind<F, Option<T>> visit(DSL.QueryMeta<T> query);

    <T> Kind<F, Iterable<T>> visit(QueryIterable<T> query);

    <T> Kind<F, Option<T>> visit(DSL.QueryOne<T> query);
  }

  record QueryIterable<T>(SQL query, Function1<Row, T> rowMapper) implements DSL<Iterable<T>> {

    public QueryIterable {
      checkNonNull(query);
      checkNonNull(rowMapper);
    }

    @Override
    public <F extends Witness> Kind<F, Iterable<T>> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }
  }

  record QueryMeta<T>(SQL query, Function1<RowMetaData, T> rowMapper) implements DSL<Option<T>> {

    public QueryMeta {
      checkNonNull(query);
      checkNonNull(rowMapper);
    }

    @Override
    public <F extends Witness> Kind<F, Option<T>> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }
  }

  record QueryOne<T>(SQL query, Function1<Row, T> rowMapper) implements DSL<Option<T>> {

    public QueryOne {
      checkNonNull(query);
      checkNonNull(rowMapper);
    }

    @Override
    public <F extends Witness> Kind<F, Option<T>> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }
  }

  record UpdateWithKeys<T>(SQL query, Field<T> field) implements DSL<Option<T>> {

    public UpdateWithKeys {
      checkNonNull(field);
      checkNonNull(field);
    }

    @Override
    public <F extends Witness> Kind<F, Option<T>> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }
  }

  record Update(SQL query) implements DSL<Unit> {

    public Update {
      checkNonNull(query);
    }

    @Override
    public <F extends Witness> Kind<F, Unit> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }
  }
}

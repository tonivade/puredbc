/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Higher1;
import com.github.tonivade.purefun.HigherKind;
import com.github.tonivade.purefun.Kind;
import com.github.tonivade.purefun.Sealed;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.Sequence;

import java.sql.ResultSet;

import static java.util.Objects.requireNonNull;

@Sealed
@HigherKind
public interface DSL<T> {

  <F extends Kind> Higher1<F, T> accept(Visitor<F> visitor);

  DSLModule getModule();

  interface Visitor<F extends Kind> {
    Higher1<F, Unit> visit(DSL.Update update);
    <T> Higher1<F, T> visit(DSL.Query<T> query);
  }

  final class Query<T> implements DSL<T> {

    private final SQL query;
    private final Function1<ResultSet, T> extractor;

    protected Query(SQL query, Function1<ResultSet, T> extractor) {
      this.query = requireNonNull(query);
      this.extractor = requireNonNull(extractor);
    }

    @Override
    public <F extends Kind> Higher1<F, T> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    public String getQuery() {
      return query.getQuery();
    }

    public Sequence<?> getParams() {
      return query.getParams();
    }

    public Function1<ResultSet, T> getExtractor() {
      return extractor;
    }

    @Override
    public DSLModule getModule() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "Query{" +
          "query=" + query +
          '}';
    }
  }

  final class Update implements DSL<Unit> {

    private final SQL query;

    protected Update(SQL query) {
      this.query = requireNonNull(query);
    }

    @Override
    public <F extends Kind> Higher1<F, Unit> accept(Visitor<F> visitor) {
      return visitor.visit(this);
    }

    public String getQuery() {
      return query.getQuery();
    }

    public Sequence<?> getParams() {
      return query.getParams();
    }

    @Override
    public DSLModule getModule() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "Update{" +
          "query=" + query +
          '}';
    }
  }
}

interface DSLModule { }
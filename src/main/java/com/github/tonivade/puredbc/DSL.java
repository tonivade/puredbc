/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.HigherKind;
import com.github.tonivade.purefun.Sealed;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.Sequence;

import java.sql.ResultSet;

import static java.util.Objects.requireNonNull;

@Sealed
@HigherKind
public interface DSL<T> {

  DSLModule getModule();

  final class Query<T> implements DSL<T> {

    private final Bindable query;
    private final Function1<ResultSet, T> extractor;

    protected Query(Bindable query, Function1<ResultSet, T> extractor) {
      this.query = requireNonNull(query);
      this.extractor = requireNonNull(extractor);
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

    private final Bindable query;

    protected Update(Bindable query) {
      this.query = requireNonNull(query);
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
/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class SQL1<A> {

  private final String query;

  protected SQL1(String query) {
    this.query = requireNonNull(query);
  }

  public SQL bind(A value) {
    return new SQL(query, arrayOf(value));
  }

  public <B> SQL2<A, B> and(String condition) {
    return new SQL2<>(query + " and " + condition);
  }

  public <B> SQL2<A, B> where(String condition) {
    return new SQL2<>(query + " where " + condition);
  }

  public SQL1<A> groupBy(String field) {
    return new SQL1<>(query + " group by " + field);
  }

  public SQL1<A> orderBy(String field) {
    return new SQL1<>(query + " order by " + field);
  }

  @Override
  public String toString() {
    return "SQL1{" +
        "query='" + query + '\'' +
        '}';
  }
}

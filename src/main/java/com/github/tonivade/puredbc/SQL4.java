/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class SQL4<A, B, C, D> {

  private final String query;

  protected SQL4(String query) {
    this.query = requireNonNull(query);
  }

  public SQL bind(A value1, B value2, C value3, D value4) {
    return new SQL(query, arrayOf(value1, value2, value3, value4));
  }

  public <E> SQL5<A, B, C, D, E> and(String condition) {
    return new SQL5<>(query + " and " + condition);
  }

  public <E> SQL5<A, B, C, D, E> where(String condition) {
    return new SQL5<>(query + " where " + condition);
  }

  public SQL4<A, B, C, D> groupBy(String field) {
    return new SQL4<>(query + " group by " + field);
  }

  public SQL4<A, B, C, D> orderBy(String field) {
    return new SQL4<>(query + " order by " + field);
  }

  @Override
  public String toString() {
    return "SQL4{" +
        "query='" + query + '\'' +
        '}';
  }
}

/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class SQL6<A, B, C, D, E, F> {

  private final String query;

  protected SQL6(String query) {
    this.query = requireNonNull(query);
  }

  public SQL bind(A a, B b, C c, D d, E e, F f) {
    return new SQL(query, arrayOf(a, b, c, d, e, f));
  }

  public SQL6<A, B, C, D, E, F> groupBy(String field) {
    return new SQL6<>(query + " group by " + field);
  }

  public SQL6<A, B, C, D, E, F> orderBy(String field) {
    return new SQL6<>(query + " order by " + field);
  }

  public SQL6<A, B, C, D, E, F> asc() {
    return new SQL6<>(query + " asc");
  }

  public SQL6<A, B, C, D, E, F> desc() {
    return new SQL6<>(query + " desc");
  }

  @Override
  public String toString() {
    return String.format("SQL6{query='%s'}", query);
  }
}

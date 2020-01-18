/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class SQL5<A, B, C, D, E> {

  private final String query;

  protected SQL5(String query) {
    this.query = requireNonNull(query);
  }

  public SQL bind(A a, B b, C c, D d, E e) {
    return new SQL(query, arrayOf(a, b, c, d, e));
  }

  public <F> SQL5<A, B, C, D, E> groupBy(Field<F> field) {
    return new SQL5<>(query + " group by " + field.name());
  }

  public <F> SQL5<A, B, C, D, E> orderBy(Field<F> field) {
    return new SQL5<>(query + " order by " + field.name());
  }

  public SQL5<A, B, C, D, E> asc() {
    return new SQL5<>(query + " asc");
  }

  public SQL5<A, B, C, D, E> desc() {
    return new SQL5<>(query + " desc");
  }

  public SQL5<A, B, C, D, E> limit(int limit) {
    return new SQL5<>(query + " limit " + limit);
  }

  public SQL5<A, B, C, D, E> offset(int offset) {
    return new SQL5<>(query + " offset " + offset);
  }

  @Override
  public String toString() {
    return String.format("SQL5{query='%s'}", query);
  }
}

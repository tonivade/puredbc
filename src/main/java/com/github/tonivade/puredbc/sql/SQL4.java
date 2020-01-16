/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class SQL4<A, B, C, D> {

  private final String query;

  protected SQL4(String query) {
    this.query = requireNonNull(query);
  }

  public SQL bind(A a, B b, C c, D d) {
    return new SQL(query, arrayOf(a, b, c, d));
  }

  public <E> SQL5<A, B, C, D, E> and(Condition<E> condition) {
    return new SQL5<>(query + " and " + condition);
  }

  public <E> SQL5<A, B, C, D, E> where(Condition<E> condition) {
    return new SQL5<>(query + " where " + condition);
  }

  public <E> SQL4<A, B, C, D> groupBy(Field<E> field) {
    return new SQL4<>(query + " group by " + field);
  }

  public <E> SQL4<A, B, C, D> orderBy(Field<E> field) {
    return new SQL4<>(query + " order by " + field);
  }

  public SQL4<A, B, C, D> asc() {
    return new SQL4<>(query + " asc");
  }

  public SQL4<A, B, C, D> desc() {
    return new SQL4<>(query + " desc");
  }

  @Override
  public String toString() {
    return String.format("SQL4{query='%s'}", query);
  }
}

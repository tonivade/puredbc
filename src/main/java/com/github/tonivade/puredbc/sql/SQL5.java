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

  public <F> SQL6<A, B, C, D, E, F> and(Condition<F> condition) {
    return new SQL6<>(query + " and " + condition);
  }

  public <F> SQL6<A, B, C, D, E, F> where(Condition<F> condition) {
    return new SQL6<>(query + " where " + condition);
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

  @Override
  public String toString() {
    return String.format("SQL5{query='%s'}", query);
  }
}

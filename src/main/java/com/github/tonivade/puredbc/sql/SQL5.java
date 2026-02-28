/*
 * Copyright (c) 2020-2026, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.core.Precondition.checkNonEmpty;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;

public final class SQL5<A, B, C, D, E> {

  private final String query;

  protected SQL5(String query) {
    this.query = checkNonEmpty(query);
  }

  public SQL bind(A a, B b, C c, D d, E e) {
    return new SQL(query, arrayOf(a, b, c, d, e));
  }

  public <F> SQL5<A, B, C, D, E> groupBy(Field<F> field) {
    return new SQL5<>(query + " group by " + field.render());
  }

  public <F> SQL5<A, B, C, D, E> orderBy(Field<F> field) {
    return new SQL5<>(query + " order by " + field.render());
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

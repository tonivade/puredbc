/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.Precondition.checkNonEmpty;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;

public final class SQL4<A, B, C, D> {

  private final String query;

  protected SQL4(String query) {
    this.query = checkNonEmpty(query);
  }

  public SQL bind(A a, B b, C c, D d) {
    return new SQL(query, arrayOf(a, b, c, d));
  }

  public <E> SQL5<A, B, C, D, E> and(Condition<E> condition) {
    return new SQL5<>(query + " and " + condition.expression());
  }

  public <E> SQL5<A, B, C, D, E> where(Condition<E> condition) {
    return new SQL5<>(query + " where " + condition.expression());
  }

  public <E> SQL4<A, B, C, D> groupBy(Field<E> field) {
    return new SQL4<>(query + " group by " + field.name());
  }

  public <E> SQL4<A, B, C, D> orderBy(Field<E> field) {
    return new SQL4<>(query + " order by " + field.name());
  }

  public SQL4<A, B, C, D> asc() {
    return new SQL4<>(query + " asc");
  }

  public SQL4<A, B, C, D> desc() {
    return new SQL4<>(query + " desc");
  }

  public SQL4<A, B, C, D> limit(int limit) {
    return new SQL4<>(query + " limit " + limit);
  }

  public SQL4<A, B, C, D> offset(int offset) {
    return new SQL4<>(query + " offset " + offset);
  }

  @Override
  public String toString() {
    return String.format("SQL4{query='%s'}", query);
  }
}

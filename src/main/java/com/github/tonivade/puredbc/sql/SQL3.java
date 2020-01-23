/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class SQL3<A, B, C> {

  private final String query;

  protected SQL3(String query) {
    this.query = requireNonNull(query);
  }

  public SQL bind(A a, B b, C c) {
    return new SQL(query, arrayOf(a, b, c));
  }

  public <D> SQL4<A, B, C, D> and(Condition<D> condition) {
    return new SQL4<>(query + " and " + condition.expression());
  }

  public <D> SQL4<A, B, C, D> where(Condition<D> condition) {
    return new SQL4<>(query + " where " + condition.expression());
  }

  public <D> SQL3<A, B, C> groupBy(Field<D> field) {
    return new SQL3<>(query + " group by " + field.name());
  }

  public <D> SQL3<A, B, C> orderBy(Field<D> field) {
    return new SQL3<>(query + " order by " + field.name());
  }

  public SQL3<A, B, C> asc() {
    return new SQL3<>(query + " asc");
  }

  public SQL3<A, B, C> desc() {
    return new SQL3<>(query + " desc");
  }

  public SQL3<A, B, C> limit(int limit) {
    return new SQL3<>(query + " limit " + limit);
  }

  public SQL3<A, B, C> offset(int offset) {
    return new SQL3<>(query + " offset " + offset);
  }

  @Override
  public String toString() {
    return String.format("SQL3{query='%s'}", query);
  }
}

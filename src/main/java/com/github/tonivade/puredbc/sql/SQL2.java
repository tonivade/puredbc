/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class SQL2<A, B> {

  private final String query;

  protected SQL2(String query) {
    this.query = requireNonNull(query);
  }

  public SQL bind(A a, B b) {
    return new SQL(query, arrayOf(a, b));
  }

  public <C> SQL3<A, B, C> and(Condition<C> condition) {
    return new SQL3<>(query + " and " + condition.expression());
  }

  public <C> SQL3<A, B, C> where(Condition<C> condition) {
    return new SQL3<>(query + " where " + condition.expression());
  }

  public <C> SQL2<A, B> groupBy(Field<C> field) {
    return new SQL2<>(query + " group by " + field.fullName());
  }

  public <C> SQL2<A, B> orderBy(Field<C> field) {
    return new SQL2<>(query + " order by " + field.fullName());
  }

  public SQL2<A, B> asc() {
    return new SQL2<>(query + " asc");
  }

  public SQL2<A, B> desc() {
    return new SQL2<>(query + " desc");
  }

  public SQL2<A, B> limit(int limit) {
    return new SQL2<>(query + " limit " + limit);
  }

  public SQL2<A, B> offset(int offset) {
    return new SQL2<>(query + " offset " + offset);
  }

  @Override
  public String toString() {
    return String.format("SQL2{query='%s'}", query);
  }
}

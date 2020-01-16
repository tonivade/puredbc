/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class SQL1<A> {

  private final String query;

  protected SQL1(String query) {
    this.query = requireNonNull(query);
  }

  public SQL bind(A a) {
    return new SQL(query, arrayOf(a));
  }

  public <B> SQL2<A, B> and(Condition<B> condition) {
    return new SQL2<>(query + " and " + condition);
  }

  public <B> SQL2<A, B> where(Condition<B> condition) {
    return new SQL2<>(query + " where " + condition);
  }

  public <B> SQL1<A> groupBy(Field<B> field) {
    return new SQL1<>(query + " group by " + field);
  }

  public <B> SQL1<A> orderBy(Field<B> field) {
    return new SQL1<>(query + " order by " + field);
  }

  public SQL1<A> asc() {
    return new SQL1<>(query + " asc");
  }

  public SQL1<A> desc() {
    return new SQL1<>(query + " desc");
  }

  @Override
  public String toString() {
    return String.format("SQL1{query='%s'}", query);
  }
}

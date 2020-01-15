/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

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

  public <C> SQL3<A, B, C> and(String condition) {
    return new SQL3<>(query + " and " + condition);
  }

  public <C> SQL3<A, B, C> where(String condition) {
    return new SQL3<>(query + " where " + condition);
  }

  public SQL2<A, B> groupBy(String field) {
    return new SQL2<>(query + " group by " + field);
  }

  public SQL2<A, B> orderBy(String field) {
    return new SQL2<>(query + " order by " + field);
  }

  public SQL2<A, B> asc() {
    return new SQL2<>(query + " asc");
  }

  public SQL2<A, B> desc() {
    return new SQL2<>(query + " desc");
  }

  @Override
  public String toString() {
    return String.format("SQL2{query='%s'}", query);
  }
}

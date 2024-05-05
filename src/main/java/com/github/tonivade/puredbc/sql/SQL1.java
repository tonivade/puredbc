/*
 * Copyright (c) 2020-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.core.Precondition.checkNonEmpty;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;

public final class SQL1<A> {

  private final String query;

  protected SQL1(String query) {
    this.query = checkNonEmpty(query);
  }

  public SQL bind(A a) {
    return new SQL(query, arrayOf(a));
  }

  public SQL1<A> from(SQL other) {
    return new SQL1<>(other.getQuery() + " from (" + query + ")");
  }

  public <B> SQL2<A, B> and(Condition<B> condition) {
    return new SQL2<>(query + " and " + condition.expression());
  }

  public <B> SQL2<A, B> where(Condition<B> condition) {
    return new SQL2<>(query + " where " + condition.expression());
  }

  public <B> SQL1<A> groupBy(Field<B> field) {
    return new SQL1<>(query + " group by " + field.render());
  }

  public <B> SQL1<A> orderBy(Field<B> field) {
    return new SQL1<>(query + " order by " + field.render());
  }

  public SQL1<A> asc() {
    return new SQL1<>(query + " asc");
  }

  public SQL1<A> desc() {
    return new SQL1<>(query + " desc");
  }

  public SQL1<A> limit(int limit) {
    return new SQL1<>(query + " limit " + limit);
  }

  public SQL1<A> offset(int offset) {
    return new SQL1<>(query + " offset " + offset);
  }

  @Override
  public String toString() {
    return String.format("SQL1{query='%s'}", query);
  }
}

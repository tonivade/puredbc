/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class Bindable2<A, B> {

  private final String query;

  protected Bindable2(String query) {
    this.query = requireNonNull(query);
  }

  public SQL bind(A value1, B value2) {
    return new SQL(query, arrayOf(value1, value2));
  }

  public <C> Bindable3<A, B, C> and(String condition) {
    return new Bindable3<>(query + " and " + condition);
  }

  public <C> Bindable3<A, B, C> where(String condition) {
    return new Bindable3<>(query + " where " + condition);
  }

  @Override
  public String toString() {
    return "Bindable2{" +
        "query='" + query + '\'' +
        '}';
  }
}

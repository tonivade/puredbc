/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class Bindable3<A, B, C> {

  private final String query;

  protected Bindable3(String query) {
    this.query = requireNonNull(query);
  }

  public Bindable bind(A value1, B value2, C value3) {
    return new Bindable(query, arrayOf(value1, value2, value3));
  }

  public <D> Bindable4<A, B, C, D> and(String condition) {
    return new Bindable4<>(query + " and " + condition);
  }

  public <D> Bindable4<A, B, C, D> where(String condition) {
    return new Bindable4<>(query + " where " + condition);
  }

  @Override
  public String toString() {
    return "Bindable3{" +
        "query='" + query + '\'' +
        '}';
  }
}

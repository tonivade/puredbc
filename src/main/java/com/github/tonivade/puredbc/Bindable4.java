/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class Bindable4<A, B, C, D> {

  private final String query;

  protected Bindable4(String query) {
    this.query = requireNonNull(query);
  }

  public Bindable bind(A value1, B value2, C value3, D value4) {
    return new Bindable(query, arrayOf(value1, value2, value3, value4));
  }

  public <E> Bindable5<A, B, C, D, E> and(String condition) {
    return new Bindable5<>(query + " and " + condition);
  }

  public <E> Bindable5<A, B, C, D, E> where(String condition) {
    return new Bindable5<>(query + " where " + condition);
  }

  @Override
  public String toString() {
    return "Bindable4{" +
        "query='" + query + '\'' +
        '}';
  }
}

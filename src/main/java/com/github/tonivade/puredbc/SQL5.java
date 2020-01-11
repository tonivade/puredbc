/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class SQL5<A, B, C, D, E> {

  private final String query;

  protected SQL5(String query) {
    this.query = requireNonNull(query);
  }

  public SQL bind(A value1, B value2, C value3, D value4, E value5) {
    return new SQL(query, arrayOf(value1, value2, value3, value4, value5));
  }

  @Override
  public String toString() {
    return "Bindable5{" +
        "query='" + query + '\'' +
        '}';
  }
}

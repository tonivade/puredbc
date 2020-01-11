/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class Bindable1<A> {

  private final String query;

  protected Bindable1(String query) {
    this.query = requireNonNull(query);
  }

  public SQL bind(A value) {
    return new SQL(query, arrayOf(value));
  }

  public <B> Bindable2<A, B> and(String condition) {
    return new Bindable2<>(query + " and " + condition);
  }

  public <B> Bindable2<A, B> where(String condition) {
    return new Bindable2<>(query + " where " + condition);
  }

  @Override
  public String toString() {
    return "Bindable1{" +
        "query='" + query + '\'' +
        '}';
  }
}

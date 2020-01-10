/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.Sequence;

import static java.util.Objects.requireNonNull;

public final class Bindable {

  private final String query;
  private Tuple values;

  private Bindable(String query) {
    this(query, null);
  }

  private Bindable(String query, Tuple values) {
    this.query = requireNonNull(query);
    this.values = values;
  }

  public String getQuery() {
    return query;
  }

  public Sequence<Object> getParams() {
    return values != null ? values.toSequence() : ImmutableList.empty();
  }

  public static Bindable of(String query) {
    return new Bindable(query);
  }

  public <A> Bindable with(A value) {
    return new Bindable(query, Tuple.of(value));
  }

  public <A, B> Bindable with(A value1, B value2) {
    return new Bindable(query, Tuple.of(value1, value2));
  }
}

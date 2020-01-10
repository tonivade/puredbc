/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.data.Sequence;

import static com.github.tonivade.purefun.data.ImmutableArray.empty;
import static java.util.Objects.requireNonNull;

public class Bindable {

  private final String query;
  private final Sequence<?> values;

  protected Bindable(String query) {
    this(query, empty());
  }

  protected Bindable(String query, Sequence<?> values) {
    this.query = requireNonNull(query);
    this.values = requireNonNull(values);
  }

  String getQuery() {
    return query;
  }

  Sequence<?> getParams() {
    return values;
  }

  public static Bindable of(String query) {
    return new Bindable(query);
  }

  public static <A> Bindable1<A> of1(String query) {
    return new Bindable1<>(query);
  }

  public static <A, B> Bindable2<A, B> of2(String query) {
    return new Bindable2<>(query);
  }

  public static <A, B, C> Bindable3<A, B, C> of3(String query) {
    return new Bindable3<>(query);
  }

  public static <A, B, C, D> Bindable4<A, B, C, D> of4(String query) {
    return new Bindable4<>(query);
  }

  public static <A, B, C, D, E> Bindable5<A, B, C, D, E> of5(String query) {
    return new Bindable5<>(query);
  }
}

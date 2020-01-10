/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.data.ImmutableArray;

public class Bindable5<A, B, C, D, E> extends Bindable {

  protected Bindable5(String query) {
    super(query);
  }

  public Bindable bind(A value1, B value2, C value3, D value4, E value5) {
    return new Bindable(getQuery(), ImmutableArray.of(value1, value2, value3, value4, value5));
  }
}

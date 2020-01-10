/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.data.ImmutableArray;

public class Bindable2<A, B> extends Bindable {

  protected Bindable2(String query) {
    super(query);
  }

  public Bindable bind(A value1, B value2) {
    return new Bindable(getQuery(), ImmutableArray.of(value1, value2));
  }
}

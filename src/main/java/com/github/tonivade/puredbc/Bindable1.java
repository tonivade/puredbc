/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.data.ImmutableArray;

public class Bindable1<A> extends Bindable {

  protected Bindable1(String query) {
    super(query);
  }

  public Bindable bind(A value) {
    return new Bindable(getQuery(), ImmutableArray.of(value));
  }
}

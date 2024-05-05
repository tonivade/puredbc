/*
 * Copyright (c) 2020-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.core.Precondition.checkNonEmpty;
import static com.github.tonivade.purefun.core.Precondition.checkNonNull;

public sealed interface Alias<T> extends Field<T> {

  @Override
  String name();
  Field<T> field();

  static <T> Alias<T> of(String alias, Field<T> field) {
    return new AliasImpl<>(alias, field);
  }
}

record AliasImpl<T>(String name, Field<T> field) implements Alias<T> {

  AliasImpl {
    checkNonEmpty(name);
    checkNonNull(field);
  }
}

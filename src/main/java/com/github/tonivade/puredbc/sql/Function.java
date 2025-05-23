/*
 * Copyright (c) 2020-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.core.Precondition.checkNonEmpty;
import static com.github.tonivade.purefun.core.Precondition.checkNonNull;
import static com.github.tonivade.purefun.data.Sequence.emptyList;
import com.github.tonivade.purefun.data.Sequence;

public sealed interface Function<T> extends Field<T> {

  @Override
  String name();
  Sequence<?> params();

  static <T> Function<T> of(String name, Field<T> field) {
    return of(name, field, emptyList());
  }

  static <T> Function<T> of(String name, Field<T> field, Sequence<?> params) {
    return new FunctionImpl<>(name, Sequence.<Object>listOf(checkNonNull(field).render()).appendAll(checkNonNull(params)));
  }
}

record FunctionImpl<T>(String name, Sequence<?> params) implements Function<T> {

  FunctionImpl {
    checkNonEmpty(name);
    checkNonNull(params);
  }
}


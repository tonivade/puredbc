/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.Precondition.checkNonEmpty;
import static com.github.tonivade.purefun.Precondition.checkNonNull;
import static com.github.tonivade.purefun.data.Sequence.emptyList;
import java.util.Objects;
import com.github.tonivade.purefun.Equal;
import com.github.tonivade.purefun.data.Sequence;

public interface Function<T> extends SealedField<T> {

  static <T> Function<T> of(String name, Field<T> field) {
    return of(name, field, emptyList());
  }

  static <T> Function<T> of(String name, Field<T> field, Sequence<?> params) {
    return new FunctionImpl<>(name, field, params);
  }
}

class FunctionImpl<T> implements Function<T> {

  private static final Equal<FunctionImpl<?>> EQUAL =
      Equal.<FunctionImpl<?>>of().comparing(x -> x.name).comparing(x -> x.params);

  private final String name;
  private final Sequence<?> params;

  FunctionImpl(String name, Field<T> field, Sequence<?> params) {
    this.name = checkNonEmpty(name);
    this.params = Sequence.<Object>listOf(checkNonNull(field).name()).appendAll(checkNonNull(params));
  }

  @Override
  public String name() {
    return name + params.join(", ", "(", ")");
  }

  @Override
  public boolean equals(Object obj) {
    return EQUAL.applyTo(this, obj);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return String.format("Function{name='%s', params=%s}", name, params);
  }
}


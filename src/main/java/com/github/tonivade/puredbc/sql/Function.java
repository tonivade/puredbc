/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.data.ImmutableArray.empty;
import static com.github.tonivade.purefun.type.Validation.mapN;
import static com.github.tonivade.purefun.type.Validation.requireNonEmpty;
import static com.github.tonivade.purefun.type.Validation.requireNonNull;

import java.util.Objects;

import com.github.tonivade.purefun.Equal;
import com.github.tonivade.purefun.data.ImmutableArray;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.type.Validation;
import com.github.tonivade.purefun.type.Validation.Result;

public interface Function<T> extends Field<T> {

  static <T> Function<T> of(String name, Field<T> field) {
    return of(name, field, empty());
  }

  static <T> Function<T> of(String name, Field<T> field, Sequence<Object> params) {
    Validation<Result<String>, Function<T>> validation =
        mapN(requireNonEmpty(name), requireNonNull(field), requireNonNull(params),
            (n, f, p) -> new FunctionImpl<>(n, ImmutableArray.<Object>of(f.name()).appendAll(p)));
    return validation.getOrElseThrow();
  }
}

class FunctionImpl<T> implements Function<T> {

  private static final Equal<FunctionImpl<?>> EQUAL =
      Equal.<FunctionImpl<?>>of().comparing(x -> x.name).comparing(x -> x.params);

  private final String name;
  private final Sequence<?> params;

  FunctionImpl(String name, Sequence<?> params) {
    this.name = name;
    this.params = params;
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


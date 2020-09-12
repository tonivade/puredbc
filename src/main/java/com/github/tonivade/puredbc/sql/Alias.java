/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.type.Validation.mapN;
import static com.github.tonivade.purefun.type.Validation.requireNonEmpty;
import static com.github.tonivade.purefun.type.Validation.requireNonNull;

import java.util.Objects;

import com.github.tonivade.purefun.Equal;
import com.github.tonivade.purefun.type.Validation;
import com.github.tonivade.purefun.type.Validation.Result;

public interface Alias<T> extends Field<T> {

  static <T> Alias<T> of(String alias, Field<T> field) {
    Validation<Result<String>, AliasImpl<T>> validation =
        mapN(requireNonEmpty(alias), requireNonNull(field), AliasImpl::new);
    return validation.getOrElseThrow();
  }
}

class AliasImpl<T> implements Alias<T> {

  private static final Equal<AliasImpl<?>> EQUAL =
      Equal.<AliasImpl<?>>of().comparing(x -> x.alias).comparing(x -> x.field);

  private final String alias;
  private final Field<T> field;

  AliasImpl(String alias, Field<T> field) {
    this.alias = alias;
    this.field = field;
  }

  @Override
  public String name() {
    return field.name() + " as " + alias;
  }

  @Override
  public boolean equals(Object obj) {
    return EQUAL.applyTo(this, obj);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alias, field);
  }

  @Override
  public String toString() {
    return String.format("Alias{alias='%s', field=%s}", alias, field);
  }
}

/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.Equal;
import com.github.tonivade.purefun.type.Validation;

import java.util.Objects;

import static com.github.tonivade.purefun.type.Validation.requireNonEmpty;
import static com.github.tonivade.purefun.type.Validation.requireNonNull;

public interface Alias<T> extends Field<T> {

  @Override
  default String fullName() {
    return name();
  }

  static <T> Alias<T> of(String alias, Field<T> field) {
    Validation<String, String> validation = requireNonEmpty(alias);
    Validation<String, Field<T>> validation1 = requireNonNull(field);
    return Validation.map2(validation, validation1, AliasImpl::new).getOrElseThrow();
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
  public Field<T> as(String alias) {
    return new AliasImpl<>(alias, this);
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

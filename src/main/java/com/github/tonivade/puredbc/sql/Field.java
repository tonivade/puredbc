/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.Equal;
import com.github.tonivade.purefun.data.Sequence;

import java.util.Objects;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static com.github.tonivade.purefun.type.Validation.requireNonEmpty;
import static java.util.Objects.requireNonNull;

public interface Field<T> {

  String name();

  default Function<T> count() {
    return Function.of("count", this);
  }

  default Function<T> min() {
    return Function.of("min", this);
  }

  default Function<T> max() {
    return Function.of("max", this);
  }

  default Function<T> sum() {
    return Function.of("sum", this);
  }

  default Function<T> avg() {
    return Function.of("avg", this);
  }

  default Function<T> coalesce(int value) {
    return Function.of("coalesce", this, arrayOf(value));
  }

  default Alias<T> as(String alias) {
    return Alias.of(alias, this);
  }

  default Field<T> alias(String alias) {
    return of(requireNonNull(alias) + "." + name());
  }

  default Condition<T> eq() {
    return Condition.eq(this);
  }

  default Condition<T> eq(Field<T> other) {
    return Condition.eq(this, other);
  }

  default Condition<T> like() {
    return Condition.like(this);
  }

  default Condition<T> notEq() {
    return Condition.notEq(this);
  }

  default Condition<T> gt() {
    return Condition.gt(this);
  }

  default Condition<T> gte() {
    return Condition.gte(this);
  }

  default Condition<T> lt() {
    return Condition.lt(this);
  }

  default Condition<T> lte() {
    return Condition.lte(this);
  }

  default Condition<T> isNull() {
    return Condition.isNull(this);
  }

  default Condition<T> isNotNull() {
    return Condition.isNotNull(this);
  }

  default Condition<Iterable<T>> in() {
    return Condition.in(this);
  }

  static <T> Field<T> of(String name) {
    return requireNonEmpty(name).<Field<T>>map(FieldImpl::new).getOrElseThrow();
  }
}

final class FieldImpl<T> implements Field<T> {

  private static final Equal<Field<?>> EQUAL = Equal.<Field<?>>of().comparing(Field::name);

  private final String name;

  FieldImpl(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return name;
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
    return String.format("Field{name='%s'}", name);
  }
}

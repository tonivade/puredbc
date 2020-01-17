/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.Equal;

import java.util.Objects;

import static com.github.tonivade.purefun.type.Validation.requireNonEmpty;

public interface Condition<T> {

  String expression();

  static <T> Condition<T> eq(Field<T> field) {
    return of(field.name() + " = ?");
  }

  static <T> Condition<T> notEq(Field<T> field) {
    return of(field.name() + " <> ?");
  }

  static <T> Condition<T> lt(Field<T> field) {
    return of(field.name() + " < ?");
  }

  static <T> Condition<T> lte(Field<T> field) {
    return of(field.name() + " <= ?");
  }

  static <T> Condition<T> gt(Field<T> field) {
    return of(field.name() + " > ?");
  }

  static <T> Condition<T> gte(Field<T> field) {
    return of(field.name() + " >= ?");
  }

  static <T> Condition<T> of(String condition) {
    return requireNonEmpty(condition).<Condition<T>>map(ConditionImpl::new).getOrElseThrow();
  }
}

final class ConditionImpl<T> implements Condition<T> {

  private static final Equal<Condition<?>> EQUAL = Equal.<Condition<?>>of().comparing(Condition::expression);

  private final String expression;

  ConditionImpl(String expression) {
    this.expression = expression;
  }

  @Override
  public String expression() {
    return expression;
  }

  @Override
  public boolean equals(Object obj) {
    return EQUAL.applyTo(this, obj);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expression);
  }

  @Override
  public String toString() {
    return String.format("Condition{expression='%s'}", expression);
  }
}

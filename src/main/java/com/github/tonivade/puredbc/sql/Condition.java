/*
 * Copyright (c) 2020-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.Precondition.checkNonEmpty;
import java.util.Objects;
import com.github.tonivade.purefun.Equal;
import com.github.tonivade.purefun.data.Range;

public interface Condition<T> {

  String expression();

  default Condition<T> not() {
    return of("not " + expression());
  }

  static <T> Condition<T> eq(Field<T> field) {
    return of(field.name() + " = ?");
  }

  static <T> Condition<T> eq(Field<T> field1, Field<T> field2) {
    return of(field1.name() + " = " + field2.name());
  }

  static <T> Condition<T> like(Field<T> field) {
    return of(field.name() + " like ?");
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

  static <T> Condition<T> isNull(Field<T> field) {
    return of(field.name() + " is null");
  }

  static <T> Condition<T> isNotNull(Field<T> field) {
    return of(field.name() + " is not null");
  }

  static Condition<Range> between(Field<? extends Number> field) {
    return of(field.name() + " between ?");
  }

  static <T> Condition<Iterable<T>> in(Field<T> field) {
    return of(field.name() + " in (?)");
  }

  static <T> Condition<T> of(String condition) {
    return new ConditionImpl<>(condition);
  }
}

final class ConditionImpl<T> implements Condition<T> {

  private static final Equal<Condition<?>> EQUAL = Equal.<Condition<?>>of().comparing(Condition::expression);

  private final String expression;

  ConditionImpl(String expression) {
    this.expression = checkNonEmpty(expression);
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

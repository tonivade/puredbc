/*
 * Copyright (c) 2020-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.core.Precondition.checkNonEmpty;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import com.github.tonivade.purefun.HigherKind;

@HigherKind
public sealed interface Field<T> extends FieldOf<T>, Renderable permits FieldImpl, Function, Alias, TableField {

  String name();

  default Alias<T> as(String alias) {
    return Alias.of(alias, this);
  }

  default TableField<T> alias(String alias) {
    return TableField.of(alias, this);
  }

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
    return new FieldImpl<>(name);
  }
}

record FieldImpl<T>(String name) implements Field<T> {

  FieldImpl {
    checkNonEmpty(name);
  }
}

/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.Precondition.checkNonEmpty;
import com.github.tonivade.purefun.HigherKind;
import com.github.tonivade.purefun.data.Range;

@HigherKind
public sealed interface Condition<T> extends ConditionOf<T> {

  String expression();

  default Condition<T> not() {
    return of("not " + expression());
  }

  static <T> Condition<T> eq(Field<T> field) {
    return of(field.render() + " = ?");
  }

  static <T> Condition<T> eq(Field<T> field1, Field<T> field2) {
    return of(field1.render() + " = " + field2.render());
  }

  static <T> Condition<T> like(Field<T> field) {
    return of(field.render() + " like ?");
  }

  static <T> Condition<T> notEq(Field<T> field) {
    return of(field.render() + " <> ?");
  }

  static <T> Condition<T> lt(Field<T> field) {
    return of(field.render() + " < ?");
  }

  static <T> Condition<T> lte(Field<T> field) {
    return of(field.render() + " <= ?");
  }

  static <T> Condition<T> gt(Field<T> field) {
    return of(field.render() + " > ?");
  }

  static <T> Condition<T> gte(Field<T> field) {
    return of(field.render() + " >= ?");
  }

  static <T> Condition<T> isNull(Field<T> field) {
    return of(field.render() + " is null");
  }

  static <T> Condition<T> isNotNull(Field<T> field) {
    return of(field.render() + " is not null");
  }

  static Condition<Range> between(Field<? extends Number> field) {
    return of(field.render() + " between ?");
  }

  static <T> Condition<Iterable<T>> in(Field<T> field) {
    return of(field.render() + " in (?)");
  }

  static <T> Condition<T> of(String condition) {
    return new ConditionImpl<>(condition);
  }
}

record ConditionImpl<T>(String expression) implements Condition<T> {

  ConditionImpl {
    checkNonEmpty(expression);
  }
}

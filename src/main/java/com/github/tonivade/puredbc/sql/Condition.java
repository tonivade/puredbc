/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

public interface Condition<T> {

  static <T> Condition<T> eq(Field<T> field) {
    return of(field + " = ?");
  }

  static <T> Condition<T> notEq(Field<T> field) {
    return of(field + " <> ?");
  }

  static <T> Condition<T> lt(Field<T> field) {
    return of(field + " < ?");
  }

  static <T> Condition<T> lte(Field<T> field) {
    return of(field + " <= ?");
  }

  static <T> Condition<T> gt(Field<T> field) {
    return of(field + " > ?");
  }

  static <T> Condition<T> gte(Field<T> field) {
    return of(field + " >= ?");
  }

  static <T> Condition<T> between(Field<T> field) {
    return of(field + " between ?");
  }

  static <T> Condition<T> of(String condition) {
    return new Condition<T>() {
      @Override
      public String toString() {
        return condition;
      }
    };
  }
}

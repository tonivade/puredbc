/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static java.util.Objects.requireNonNull;

public interface Field<T> {

  String name();

  default Field<T> count() {
    return of("count(" + name() + ")");
  }

  default Field<T> as(String alias) {
    return of(name() + " as " + alias);
  }

  default Field<T> alias(String alias) {
    return of(requireNonNull(alias) + "." + name());
  }

  default Condition<T> eq() {
    return Condition.eq(this);
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

  static <T> Field<T> of(String name) {
    return () -> name;
  }
}

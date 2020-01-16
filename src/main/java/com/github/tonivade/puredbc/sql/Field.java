/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

public interface Field<T> {

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

  default Condition<T> between() {
    return Condition.between(this);
  }

  static <T> Field<T> of(String name) {
    return new Field<T>() {
      @Override
      public String toString() {
        return name;
      }
    };
  }
}

/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import java.sql.ResultSet;

public interface Result {

  <T> T unwrap();

  @SuppressWarnings("unchecked")
  static Result wrap(ResultSet result) {
    return new Result() {
      @Override
      public <T> T unwrap() {
        return (T) result;
      }
    };
  }
}

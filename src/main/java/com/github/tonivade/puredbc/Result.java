/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Tuple;

import java.sql.ResultSet;

public interface Result {

  Row row();

  <T> T unwrap();

  @SuppressWarnings("unchecked")
  static Result wrap(ResultSet result) {
    return new Result() {

      @Override
      public Row row() {
        return new JdbcRow(result);
      }

      @Override
      public <T> T unwrap() {
        return (T) result;
      }
    };
  }

  @SuppressWarnings("unchecked")
  static Result wrap(io.r2dbc.spi.Row row, io.r2dbc.spi.RowMetadata meta) {
    return new Result() {

      @Override
      public Row row() {
        return new R2dbcRow(row);
      }

      @Override
      public <T> T unwrap() {
        return (T) Tuple.of(row, meta);
      }
    };
  }
}

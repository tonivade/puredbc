/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.purefun.type.Try;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

import static java.util.Objects.requireNonNull;

final class JdbcRow implements Row {

  private final ResultSet resultSet;

  protected JdbcRow(ResultSet resultSet) {
    this.resultSet = requireNonNull(resultSet);
  }

  @Override
  public Try<String> getString(Field<String> field) {
    return Try.of(() -> resultSet.getString(field.name()));
  }

  @Override
  public Try<Integer> getInteger(Field<Integer> field) {
    return Try.of(() -> resultSet.getObject(field.name(), Integer.class));
  }

  @Override
  public Try<Long> getLong(Field<Long> field) {
    return Try.of(() -> resultSet.getObject(field.name(), Long.class));
  }

  @Override
  public Try<Short> getShort(Field<Short> field) {
    return Try.of(() -> resultSet.getObject(field.name(), Short.class));
  }

  @Override
  public Try<Byte> getByte(Field<Byte> field) {
    return Try.of(() -> resultSet.getObject(field.name(), Byte.class));
  }

  @Override
  public Try<Float> getFloat(Field<Float> field) {
    return Try.of(() -> resultSet.getObject(field.name(), Float.class));
  }

  @Override
  public Try<Double> getDouble(Field<Double> field) {
    return Try.of(() -> resultSet.getObject(field.name(), Double.class));
  }

  @Override
  public Try<BigDecimal> getBigDecimal(Field<BigDecimal> field) {
    return Try.of(() -> resultSet.getBigDecimal(field.name()));
  }

  @Override
  public Try<Boolean> getBoolean(Field<Boolean> field) {
    return Try.of(() -> resultSet.getObject(field.name(), Boolean.class));
  }

  @Override
  public Try<Date> getTimestamp(Field<Date> field) {
    return Try.of(() -> resultSet.getTimestamp(field.name()));
  }

  @Override
  public Try<Date> getDate(Field<Date> field) {
    return Try.of(() -> resultSet.getDate(field.name()));
  }

  @Override
  public Try<Date> getTime(Field<Date> field) {
    return Try.of(() -> resultSet.getTime(field.name()));
  }
}

/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.purefun.type.Try;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Date;

import static java.util.Objects.requireNonNull;

final class R2dbcRow implements Row {

  private final io.r2dbc.spi.Row impl;

  protected R2dbcRow(io.r2dbc.spi.Row impl) {
    this.impl = requireNonNull(impl);
  }

  @Override
  public Try<String> getString(Field<String> field) {
    return Try.of(() -> impl.get(field.name(), String.class));
  }

  @Override
  public Try<Integer> getInteger(Field<Integer> field) {
    return Try.of(() -> impl.get(field.name(), Integer.class));
  }

  @Override
  public Try<Long> getLong(Field<Long> field) {
    return Try.of(() -> impl.get(field.name(), Long.class));
  }

  @Override
  public Try<Short> getShort(Field<Short> field) {
    return Try.of(() -> impl.get(field.name(), Short.class));
  }

  @Override
  public Try<Byte> getByte(Field<Byte> field) {
    return Try.of(() -> impl.get(field.name(), Byte.class));
  }

  @Override
  public Try<Float> getFloat(Field<Float> field) {
    return Try.of(() -> impl.get(field.name(), Float.class));
  }

  @Override
  public Try<Double> getDouble(Field<Double> field) {
    return Try.of(() -> impl.get(field.name(), Double.class));
  }

  @Override
  public Try<BigDecimal> getBigDecimal(Field<BigDecimal> field) {
    return Try.of(() -> impl.get(field.name(), BigDecimal.class));
  }

  @Override
  public Try<Boolean> getBoolean(Field<Boolean> field) {
    return Try.of(() -> impl.get(field.name(), Boolean.class));
  }

  @Override
  public Try<java.util.Date> getTimestamp(Field<java.util.Date> field) {
    return Try.of(() -> impl.get(field.name(), Timestamp.class));
  }

  @Override
  public Try<java.util.Date> getDate(Field<java.util.Date> field) {
    return Try.of(() -> impl.get(field.name(), Date.class));
  }

  @Override
  public Try<java.util.Date> getTime(Field<java.util.Date> field) {
    return Try.of(() -> impl.get(field.name(), Time.class));
  }
}

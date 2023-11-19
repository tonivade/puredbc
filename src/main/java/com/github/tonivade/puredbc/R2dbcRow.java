/*
 * Copyright (c) 2020-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.Precondition.checkNonNull;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import com.github.tonivade.puredbc.sql.Field;

final class R2dbcRow implements Row {

  private final io.r2dbc.spi.Row impl;

  R2dbcRow(io.r2dbc.spi.Row impl) {
    this.impl = checkNonNull(impl);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(Field<T> field) {
    return (T) impl.get(field.name());
  }

  @Override
  public String getString(Field<String> field) {
    return impl.get(field.name(), String.class);
  }

  @Override
  public Integer getInteger(Field<Integer> field) {
    return impl.get(field.name(), Integer.class);
  }

  @Override
  public Long getLong(Field<Long> field) {
    return impl.get(field.name(), Long.class);
  }

  @Override
  public Short getShort(Field<Short> field) {
    return impl.get(field.name(), Short.class);
  }

  @Override
  public Byte getByte(Field<Byte> field) {
    return impl.get(field.name(), Byte.class);
  }

  @Override
  public Float getFloat(Field<Float> field) {
    return impl.get(field.name(), Float.class);
  }

  @Override
  public Double getDouble(Field<Double> field) {
    return impl.get(field.name(), Double.class);
  }

  @Override
  public BigDecimal getBigDecimal(Field<BigDecimal> field) {
    return impl.get(field.name(), BigDecimal.class);
  }

  @Override
  public Boolean getBoolean(Field<Boolean> field) {
    return impl.get(field.name(), Boolean.class);
  }

  @Override
  public java.util.Date getTimestamp(Field<java.util.Date> field) {
    return impl.get(field.name(), Timestamp.class);
  }

  @Override
  public java.util.Date getDate(Field<java.util.Date> field) {
    return impl.get(field.name(), Date.class);
  }

  @Override
  public java.util.Date getTime(Field<java.util.Date> field) {
    return impl.get(field.name(), Time.class);
  }
}

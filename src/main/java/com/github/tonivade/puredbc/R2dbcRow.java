/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
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

  protected R2dbcRow(io.r2dbc.spi.Row impl) {
    this.impl = checkNonNull(impl);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(Field<T> field) {
    return (T) impl.get(field.render());
  }

  @Override
  public String getString(Field<String> field) {
    return impl.get(field.render(), String.class);
  }

  @Override
  public Integer getInteger(Field<Integer> field) {
    return impl.get(field.render(), Integer.class);
  }

  @Override
  public Long getLong(Field<Long> field) {
    return impl.get(field.render(), Long.class);
  }

  @Override
  public Short getShort(Field<Short> field) {
    return impl.get(field.render(), Short.class);
  }

  @Override
  public Byte getByte(Field<Byte> field) {
    return impl.get(field.render(), Byte.class);
  }

  @Override
  public Float getFloat(Field<Float> field) {
    return impl.get(field.render(), Float.class);
  }

  @Override
  public Double getDouble(Field<Double> field) {
    return impl.get(field.render(), Double.class);
  }

  @Override
  public BigDecimal getBigDecimal(Field<BigDecimal> field) {
    return impl.get(field.render(), BigDecimal.class);
  }

  @Override
  public Boolean getBoolean(Field<Boolean> field) {
    return impl.get(field.render(), Boolean.class);
  }

  @Override
  public java.util.Date getTimestamp(Field<java.util.Date> field) {
    return impl.get(field.render(), Timestamp.class);
  }

  @Override
  public java.util.Date getDate(Field<java.util.Date> field) {
    return impl.get(field.render(), Date.class);
  }

  @Override
  public java.util.Date getTime(Field<java.util.Date> field) {
    return impl.get(field.render(), Time.class);
  }
}

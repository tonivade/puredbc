/*
 * Copyright (c) 2020-2023, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.core.Precondition.checkNonNull;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;
import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.purefun.core.Producer;
import com.github.tonivade.purefun.core.Recoverable;

final class JdbcRow implements Row, Recoverable {

  private final ResultSet resultSet;

  JdbcRow(ResultSet resultSet) {
    this.resultSet = checkNonNull(resultSet);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(Field<T> field) {
    return (T) run(() -> resultSet.getObject(field.name()));
  }

  @Override
  public String getString(Field<String> field) {
    return run(() -> resultSet.getString(field.name()));
  }

  @Override
  public Integer getInteger(Field<Integer> field) {
    return run(() -> resultSet.getObject(field.name(), Integer.class));
  }

  @Override
  public Long getLong(Field<Long> field) {
    return run(() -> resultSet.getObject(field.name(), Long.class));
  }

  @Override
  public Short getShort(Field<Short> field) {
    return run(() -> resultSet.getObject(field.name(), Short.class));
  }

  @Override
  public Byte getByte(Field<Byte> field) {
    return run(() -> resultSet.getObject(field.name(), Byte.class));
  }

  @Override
  public Float getFloat(Field<Float> field) {
    return run(() -> resultSet.getObject(field.name(), Float.class));
  }

  @Override
  public Double getDouble(Field<Double> field) {
    return run(() -> resultSet.getObject(field.name(), Double.class));
  }

  @Override
  public BigDecimal getBigDecimal(Field<BigDecimal> field) {
    return run(() -> resultSet.getBigDecimal(field.name()));
  }

  @Override
  public Boolean getBoolean(Field<Boolean> field) {
    return run(() -> resultSet.getObject(field.name(), Boolean.class));
  }

  @Override
  public Date getTimestamp(Field<Date> field) {
    return run(() -> resultSet.getTimestamp(field.name()));
  }

  @Override
  public Date getDate(Field<Date> field) {
    return run(() -> resultSet.getDate(field.name()));
  }

  @Override
  public Date getTime(Field<Date> field) {
    return run(() -> resultSet.getTime(field.name()));
  }

  private static <T> T run(Producer<T> producer) {
    return producer.get();
  }
}

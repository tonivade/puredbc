/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.Precondition.checkNonNull;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;
import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.purefun.Producer;
import com.github.tonivade.purefun.Recoverable;

final class JdbcRow implements Row, Recoverable {

  private final ResultSet resultSet;

  protected JdbcRow(ResultSet resultSet) {
    this.resultSet = checkNonNull(resultSet);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(Field<T> field) {
    return (T) run(() -> resultSet.getObject(field.render()));
  }

  @Override
  public String getString(Field<String> field) {
    return run(() -> resultSet.getString(field.render()));
  }

  @Override
  public Integer getInteger(Field<Integer> field) {
    return run(() -> resultSet.getObject(field.render(), Integer.class));
  }

  @Override
  public Long getLong(Field<Long> field) {
    return run(() -> resultSet.getObject(field.render(), Long.class));
  }

  @Override
  public Short getShort(Field<Short> field) {
    return run(() -> resultSet.getObject(field.render(), Short.class));
  }

  @Override
  public Byte getByte(Field<Byte> field) {
    return run(() -> resultSet.getObject(field.render(), Byte.class));
  }

  @Override
  public Float getFloat(Field<Float> field) {
    return run(() -> resultSet.getObject(field.render(), Float.class));
  }

  @Override
  public Double getDouble(Field<Double> field) {
    return run(() -> resultSet.getObject(field.render(), Double.class));
  }

  @Override
  public BigDecimal getBigDecimal(Field<BigDecimal> field) {
    return run(() -> resultSet.getBigDecimal(field.render()));
  }

  @Override
  public Boolean getBoolean(Field<Boolean> field) {
    return run(() -> resultSet.getObject(field.render(), Boolean.class));
  }

  @Override
  public Date getTimestamp(Field<Date> field) {
    return run(() -> resultSet.getTimestamp(field.render()));
  }

  @Override
  public Date getDate(Field<Date> field) {
    return run(() -> resultSet.getDate(field.render()));
  }

  @Override
  public Date getTime(Field<Date> field) {
    return run(() -> resultSet.getTime(field.render()));
  }

  private static <T> T run(Producer<T> producer) {
    return producer.get();
  }
}

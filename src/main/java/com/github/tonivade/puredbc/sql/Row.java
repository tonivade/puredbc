/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static java.util.Objects.requireNonNull;

public final class Row {

  private final ResultSet resultSet;

  public Row(ResultSet resultSet) {
    this.resultSet = requireNonNull(resultSet);
  }

  public String getString(Field<String> field) throws SQLException {
    return resultSet.getString(field.name());
  }

  public Integer getInteger(Field<Integer> field) throws SQLException {
    return resultSet.getObject(field.name(), Integer.class);
  }

  public Long getLong(Field<Long> field) throws SQLException {
    return resultSet.getObject(field.name(), Long.class);
  }

  public Short getShort(Field<Short> field) throws SQLException {
    return resultSet.getObject(field.name(), Short.class);
  }

  public Byte getByte(Field<Byte> field) throws SQLException {
    return resultSet.getObject(field.name(), Byte.class);
  }

  public Float getFloat(Field<Float> field) throws SQLException {
    return resultSet.getObject(field.name(), Float.class);
  }

  public Double getDouble(Field<Double> field) throws SQLException {
    return resultSet.getObject(field.name(), Double.class);
  }

  public BigDecimal getBigDecimal(Field<BigDecimal> field) throws SQLException {
    return resultSet.getBigDecimal(field.name());
  }

  public Boolean getBoolean(Field<Boolean> field) throws SQLException {
    return resultSet.getObject(field.name(), Boolean.class);
  }

  public Date getTimestamp(Field<Date> field) throws SQLException {
    return resultSet.getTimestamp(field.name());
  }

  public Date getDate(Field<Date> field) throws SQLException {
    return resultSet.getDate(field.name());
  }

  public Date getTime(Field<Date> field) throws SQLException {
    return resultSet.getTime(field.name());
  }
}

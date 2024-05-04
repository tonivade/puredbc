/*
 * Copyright (c) 2020-2023, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.puredbc.sql.Field;

import java.math.BigDecimal;
import java.util.Date;

public interface Row {
  
  <T> T get(Field<T> field);

  String getString(Field<String> field);

  Integer getInteger(Field<Integer> field);

  Long getLong(Field<Long> field);

  Short getShort(Field<Short> field);

  Byte getByte(Field<Byte> field);

  Float getFloat(Field<Float> field);

  Double getDouble(Field<Double> field);

  BigDecimal getBigDecimal(Field<BigDecimal> field);

  Boolean getBoolean(Field<Boolean> field);

  Date getTimestamp(Field<Date> field);

  Date getDate(Field<Date> field);

  Date getTime(Field<Date> field);
}

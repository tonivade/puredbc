/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.purefun.type.Try;

import java.math.BigDecimal;
import java.util.Date;

public interface Row {

  Try<String> getString(Field<String> field);

  Try<Integer> getInteger(Field<Integer> field);

  Try<Long> getLong(Field<Long> field);

  Try<Short> getShort(Field<Short> field);

  Try<Byte> getByte(Field<Byte> field);

  Try<Float> getFloat(Field<Float> field);

  Try<Double> getDouble(Field<Double> field);

  Try<BigDecimal> getBigDecimal(Field<BigDecimal> field);

  Try<Boolean> getBoolean(Field<Boolean> field);

  Try<Date> getTimestamp(Field<Date> field);

  Try<Date> getDate(Field<Date> field);

  Try<Date> getTime(Field<Date> field);
}

/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.Tuple2;

import java.sql.SQLException;

public interface Table2<A, B> extends Table {
  Tuple2<A, B> asTuple(Row row) throws SQLException;
}

/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.Tuple5;

import java.sql.SQLException;

public interface Table5<A, B, C, D, E> extends Table {
  Tuple5<A, B, C, D, E> asTuple(Row row) throws SQLException;
}

/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.Tuple4;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Table4<A, B, C, D> extends Table {
  Tuple4<A, B, C, D> asTuple(ResultSet rs) throws SQLException;
}

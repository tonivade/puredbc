/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.Tuple3;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Table3<A, B, C> extends Table {
  Tuple3<A, B, C> asTuple(ResultSet rs) throws SQLException;
}

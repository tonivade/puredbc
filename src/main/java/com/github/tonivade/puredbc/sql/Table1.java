/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.Tuple1;

import java.sql.SQLException;

public interface Table1<A> extends Table {
  Tuple1<A> asTuple(Row row) throws SQLException;
}

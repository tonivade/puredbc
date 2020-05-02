/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.Tuple3;
import com.github.tonivade.purefun.type.Try;

public interface Table3<A, B, C> extends Table {
  Try<Tuple3<A, B, C>> asTuple(Row row);
}

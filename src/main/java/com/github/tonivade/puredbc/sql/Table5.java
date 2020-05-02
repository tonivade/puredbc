/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.Tuple5;
import com.github.tonivade.purefun.type.Try;

public interface Table5<A, B, C, D, E> extends Table {
  Try<Tuple5<A, B, C, D, E>> asTuple(Row row);
}

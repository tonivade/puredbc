/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.Tuple1;
import com.github.tonivade.purefun.type.Try;

public interface Table1<A> extends Table {
  Try<Tuple1<A>> asTuple(Row row);
}

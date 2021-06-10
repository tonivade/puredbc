/*
 * Copyright (c) 2020-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.Tuple3;
import com.github.tonivade.purefun.typeclasses.TupleK3;

public interface Table3<A, B, C> extends Table<Tuple3<A, B, C>, TupleK3<Field_, A, B, C>> {
  
}

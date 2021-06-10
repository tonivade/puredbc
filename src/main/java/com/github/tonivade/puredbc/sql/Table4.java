/*
 * Copyright (c) 2020-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.Tuple4;
import com.github.tonivade.purefun.typeclasses.TupleK4;

public interface Table4<A, B, C, D> extends Table<Tuple4<A, B, C, D>, TupleK4<Field_, A, B, C, D>> {

}

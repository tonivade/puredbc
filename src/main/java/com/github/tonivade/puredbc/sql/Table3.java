/*
 * Copyright (c) 2020-2026, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.core.Tuple;
import com.github.tonivade.purefun.core.Tuple3;
import com.github.tonivade.purefun.typeclasses.TupleK3;

public interface Table3<A, B, C> extends Table<Tuple3<A, B, C>, TupleK3<Field<?>, A, B, C>> {

  default Field<A> field1() {
    return fields().get1().fix(FieldOf::toField);
  }

  default Field<B> field2() {
    return fields().get2().fix(FieldOf::toField);
  }

  default Field<C> field3() {
    return fields().get3().fix(FieldOf::toField);
  }

  @Override
  default Tuple3<A, B, C> asTuple(Row row) {
    return Tuple.of(row.get(field1()), row.get(field2()), row.get(field3()));
  }
}

/*
 * Copyright (c) 2020-2026, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.core.Tuple;
import com.github.tonivade.purefun.core.Tuple4;
import com.github.tonivade.purefun.typeclasses.TupleK4;

public interface Table4<A, B, C, D> extends Table<Tuple4<A, B, C, D>, TupleK4<Field<?>, A, B, C, D>> {

  default Field<A> field1() {
    return fields().get1().fix(FieldOf::toField);
  }

  default Field<B> field2() {
    return fields().get2().fix(FieldOf::toField);
  }

  default Field<C> field3() {
    return fields().get3().fix(FieldOf::toField);
  }

  default Field<D> field4() {
    return fields().get4().fix(FieldOf::toField);
  }

  @Override
  default Tuple4<A, B, C, D> asTuple(Row row) {
    return Tuple.of(row.get(field1()), row.get(field2()), row.get(field3()), row.get(field4()));
  }
}

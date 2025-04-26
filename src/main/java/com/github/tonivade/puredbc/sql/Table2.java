/*
 * Copyright (c) 2020-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.core.Tuple;
import com.github.tonivade.purefun.core.Tuple2;
import com.github.tonivade.purefun.typeclasses.TupleK2;

public interface Table2<A, B> extends Table<Tuple2<A, B>, TupleK2<Field<?>, A, B>> {

  default Field<A> field1() {
    return fields().get1().fix(FieldOf::toField);
  }

  default Field<B> field2() {
    return fields().get2().fix(FieldOf::toField);
  }

  @Override
  default Tuple2<A, B> asTuple(Row row) {
    return Tuple.of(row.get(field1()), row.get(field2()));
  }
}

/*
 * Copyright (c) 2020-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.core.Tuple;
import com.github.tonivade.purefun.core.Tuple1;
import com.github.tonivade.purefun.typeclasses.TupleK1;

public interface Table1<A> extends Table<Tuple1<A>, TupleK1<Field<?>, A>> {

  default Field<A> field1() {
    return fields().get1().fix(FieldOf::toField);
  }

  @Override
  default Tuple1<A> asTuple(Row row) {
    return Tuple.of(row.get(field1()));
  }
}

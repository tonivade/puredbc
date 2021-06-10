/*
 * Copyright (c) 2020-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple4;
import com.github.tonivade.purefun.typeclasses.TupleK4;

public interface Table4<A, B, C, D> extends Table<Tuple4<A, B, C, D>, TupleK4<Field_, A, B, C, D>> {

  @Override
  default Tuple4<A, B, C, D> asTuple(Row row) {
    TupleK4<Field_, A, B, C, D> fields = fields();
    Field<A> field1 = fields.get1().fix(FieldOf.toField());
    Field<B> field2 = fields.get2().fix(FieldOf.toField());
    Field<C> field3 = fields.get3().fix(FieldOf.toField());
    Field<D> field4 = fields.get4().fix(FieldOf.toField());
    return Tuple.of(row.get(field1), row.get(field2), row.get(field3), row.get(field4));
  }
}

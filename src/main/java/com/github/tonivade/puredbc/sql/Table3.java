/*
 * Copyright (c) 2020-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple3;
import com.github.tonivade.purefun.typeclasses.TupleK3;

public interface Table3<A, B, C> extends Table<Tuple3<A, B, C>, TupleK3<Field_, A, B, C>> {
  
  @Override
  default Tuple3<A, B, C> asTuple(Row row) {
    TupleK3<Field_, A, B, C> fields = fields();
    Field<A> field1 = fields.get1().fix(FieldOf.toField());
    Field<B> field2 = fields.get2().fix(FieldOf.toField());
    Field<C> field3 = fields.get3().fix(FieldOf.toField());
    return Tuple.of(row.get(field1), row.get(field2), row.get(field3));
  }
}

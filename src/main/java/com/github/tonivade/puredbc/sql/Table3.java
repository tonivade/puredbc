/*
 * Copyright (c) 2020-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.puredbc.sql.FieldOf.toField;
import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple3;
import com.github.tonivade.purefun.typeclasses.TupleK3;

public interface Table3<A, B, C> extends Table<Tuple3<A, B, C>, TupleK3<Field_, A, B, C>> {
  
  default Field<A> field1() {
    return fields().get1().fix(toField());
  }
  
  default Field<B> field2() {
    return fields().get2().fix(toField());
  }
  
  default Field<C> field3() {
    return fields().get3().fix(toField());
  }
  
  @Override
  default Tuple3<A, B, C> asTuple(Row row) {
    return Tuple.of(row.get(field1()), row.get(field2()), row.get(field3()));
  }
}

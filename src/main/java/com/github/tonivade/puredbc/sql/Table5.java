/*
 * Copyright (c) 2020-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.puredbc.sql.FieldOf.toField;
import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple5;
import com.github.tonivade.purefun.typeclasses.TupleK5;

public interface Table5<A, B, C, D, E> extends Table<Tuple5<A, B, C, D, E>, TupleK5<Field_, A, B, C, D, E>> {
  
  default Field<A> field1() {
    return fields().get1().fix(toField());
  }
  
  default Field<B> field2() {
    return fields().get2().fix(toField());
  }
  
  default Field<C> field3() {
    return fields().get3().fix(toField());
  }
  
  default Field<D> field4() {
    return fields().get4().fix(toField());
  }
  
  default Field<E> field5() {
    return fields().get5().fix(toField());
  }

  @Override
  default Tuple5<A, B, C, D, E> asTuple(Row row) {
    return Tuple.of(row.get(field1()), row.get(field2()), row.get(field3()), row.get(field4()), row.get(field5()));
  }
}

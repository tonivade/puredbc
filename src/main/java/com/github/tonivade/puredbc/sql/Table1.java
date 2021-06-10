/*
 * Copyright (c) 2020-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.puredbc.sql.FieldOf.toField;
import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple1;
import com.github.tonivade.purefun.typeclasses.TupleK1;

public interface Table1<A> extends Table<Tuple1<A>, TupleK1<Field_, A>> {
  
  @Override
  default Tuple1<A> asTuple(Row row) {
    TupleK1<Field_, A> fields = fields();
    Field<A> field1 = fields.get1().fix(toField());
    return Tuple.of(row.get(field1));
  }
}

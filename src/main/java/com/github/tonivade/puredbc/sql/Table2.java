/*
 * Copyright (c) 2020-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.puredbc.sql.FieldOf.toField;
import com.github.tonivade.puredbc.Row;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.typeclasses.TupleK2;

public interface Table2<A, B> extends Table<Tuple2<A, B>, TupleK2<Field_, A, B>> {
  
  @Override
  default Tuple2<A, B> asTuple(Row row) {
    TupleK2<Field_, A, B> fields = fields();
    Field<A> field1 = fields.get1().fix(toField());
    Field<B> field2 = fields.get2().fix(toField());
    return Tuple.of(row.get(field1), row.get(field2));
  }
}

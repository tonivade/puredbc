/*
 * Copyright (c) 2020-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.core.Function1.identity;
import static com.github.tonivade.purefun.core.Unit.unit;
import static com.github.tonivade.purefun.type.Validation.invalid;
import static com.github.tonivade.purefun.type.Validation.valid;

import java.util.ArrayList;
import java.util.List;

import com.github.tonivade.puredbc.Row;
import com.github.tonivade.puredbc.RowMetaData;
import com.github.tonivade.purefun.core.Tuple;
import com.github.tonivade.purefun.core.Unit;
import com.github.tonivade.purefun.data.Finisher;
import com.github.tonivade.purefun.data.ImmutableMap;
import com.github.tonivade.purefun.data.NonEmptyList;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.type.Validation;
import com.github.tonivade.purefun.typeclasses.TupleK;

public interface Table<T extends Tuple, F extends TupleK<Field<?>>> {

  String name();

  F fields();

  T asTuple(Row row);

  default Table<T, F> as(String alias) {
    throw new UnsupportedOperationException("not implemented");
  }

  default NonEmptyList<Field<?>> all() {
    Sequence<Field<?>> sequence = fields().toSequence().map(FieldOf::toField);
    return NonEmptyList.of(sequence.asList());
  }

  default Validation<Iterable<String>, Unit> validate(RowMetaData metaData) {
    List<String> result = new ArrayList<>(metaData.columnCount());
    for (var field : all()) {
      metaData.column(field.name()).ifEmpty(() -> result.add(field.name() + " not found"));
    }
    ImmutableMap<String, Field<?>> map = map();
    for (var column : metaData.allColumns()) {
      map.get(column.name().toUpperCase()).ifEmpty(() -> result.add(column.name() + " not mapped"));
    }
    return result.isEmpty() ? valid(unit()) : invalid(result);
  }

  private ImmutableMap<String, Field<?>> map() {
    return all().pipeline()
        .finish(input -> Finisher.toImmutableMap(input, f -> f.name().toUpperCase(), identity()));
  }
}

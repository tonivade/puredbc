/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.puredbc.RowMetaData;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.NonEmptyList;
import com.github.tonivade.purefun.type.Validation;

import java.util.ArrayList;
import java.util.List;

import static com.github.tonivade.purefun.Unit.unit;
import static com.github.tonivade.purefun.type.Validation.invalid;
import static com.github.tonivade.purefun.type.Validation.valid;

public interface Table {
  NonEmptyList<Field<?>> all();
  String name();

  default Validation<Iterable<String>, Unit> validate(RowMetaData metaData) {
    NonEmptyList<Field<?>> all = all();
    List<String> result = new ArrayList<>(metaData.columnCount());
    for (Field<?> field : all) {
      metaData.column(field.name()).ifEmpty(() -> result.add(field.name() + " not found"));
    }
    return result.isEmpty() ? valid(unit()) : invalid(result);
  }
}

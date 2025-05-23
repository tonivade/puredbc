/*
 * Copyright (c) 2020-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.core.Precondition.checkNonEmpty;
import static com.github.tonivade.purefun.core.Precondition.checkNonNull;

public sealed interface TableField<T> extends Field<T> {

  @Override
  default String name() {
    return field().name();
  }

  String table();
  Field<T> field();

  static <T> TableField<T> of(String alias, Field<T> name) {
    return new TableFieldImpl<>(alias, name);
  }
}

record TableFieldImpl<T>(String table, Field<T> field) implements TableField<T> {

  TableFieldImpl {
    checkNonEmpty(table);
    checkNonNull(field);
  }
}

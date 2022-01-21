/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.Precondition.checkNonEmpty;

public sealed interface TableField<T> extends Field<T> {

  static <T> TableField<T> of(String alias, String name) {
    return new TableFieldImpl<>(alias, name);
  }
}

record TableFieldImpl<T>(String table, String name) implements TableField<T> {

  TableFieldImpl {
    checkNonEmpty(table);
    checkNonEmpty(name);
  }

  @Override
  public String render() {
    return table + "." + name;
  }
}

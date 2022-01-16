/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.Precondition.checkNonEmpty;
import java.util.Objects;
import com.github.tonivade.purefun.Equal;

public sealed interface TableField<T> extends Field<T> {

  static <T> TableField<T> of(String alias, String name) {
    return new TableFieldImpl<>(alias, name);
  }
}

final class TableFieldImpl<T> implements TableField<T> {

  private static final Equal<TableFieldImpl<?>> EQUAL =
      Equal.<TableFieldImpl<?>>of().comparing(x -> x.table).comparing(x -> x.name);

  private final String table;
  private final String name;

  TableFieldImpl(String table, String name) {
    this.table = checkNonEmpty(table);
    this.name = checkNonEmpty(name);
  }

  @Override
  public String name() {
    return table + "." + name;
  }

  @Override
  public boolean equals(Object obj) {
    return EQUAL.applyTo(this, obj);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return String.format("TableField{table=%s, name='%s'}", table, name);
  }
}

/*
 * Copyright (c) 2020-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.type.Validation.mapN;
import static com.github.tonivade.purefun.type.Validation.requireNonEmpty;

import java.util.Objects;

import com.github.tonivade.purefun.Equal;
import com.github.tonivade.purefun.type.Validation;
import com.github.tonivade.purefun.type.Validation.Result;

public interface TableField<T> extends Field<T> {

  static <T> TableField<T> of(String alias, String name) {
    Validation<Result<String>, TableField<T>> validation =
        mapN(requireNonEmpty(alias), requireNonEmpty(name), TableFieldImpl::new);
    return validation.getOrElseThrow();
  }
}

final class TableFieldImpl<T> implements TableField<T> {

  private static final Equal<TableFieldImpl<?>> EQUAL =
      Equal.<TableFieldImpl<?>>of().comparing(x -> x.table).comparing(x -> x.name);

  private final String table;
  private final String name;

  TableFieldImpl(String table, String name) {
    this.table = table;
    this.name = name;
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

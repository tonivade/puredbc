/*
 * Copyright (c) 2020-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.Precondition.checkNonNull;
import com.github.tonivade.purefun.type.Option;

public interface RowMetaData {

  int columnCount();
  Iterable<String> columnNames();
  Iterable<ColumnMetaData> allColumns();
  Option<ColumnMetaData> column(String name);
  Option<ColumnMetaData> column(int index);

  final class ColumnMetaData {

    private final String name;
    private final Class<?> type;
    private final Boolean nullable;

    protected ColumnMetaData(String name, Class<?> type, Boolean nullable) {
      this.name = checkNonNull(name);
      this.type = checkNonNull(type);
      this.nullable = nullable;
    }

    public String getName() {
      return name;
    }

    public Class<?> getType() {
      return type;
    }

    public Option<Boolean> isNullable() {
      return Option.of(nullable);
    }
  }
}

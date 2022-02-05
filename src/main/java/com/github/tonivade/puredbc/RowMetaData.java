/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
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

  record ColumnMetaData(String name, Class<?> type, Boolean nullable) {

    public ColumnMetaData {
      checkNonNull(name);
      checkNonNull(type);
    }
  }
}

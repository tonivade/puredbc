/*
 * Copyright (c) 2020-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Recoverable;
import com.github.tonivade.purefun.type.Option;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

final class JdbcRowMetaData implements RowMetaData, Recoverable {

  private final Map<String, ColumnMetaData> columnsByName;
  private final Map<Integer, ColumnMetaData> columnsByIndex;

  protected JdbcRowMetaData(ResultSetMetaData impl) {
    columnsByName = new HashMap<>();
    columnsByIndex = new HashMap<>();
    try {
      for (int i = 1; i <= impl.getColumnCount(); i++) {
        var columnMetadata = createColumn(impl, i);
        columnsByIndex.put(i, columnMetadata);
        columnsByName.put(columnMetadata.name().toUpperCase(), columnMetadata);
      }
    } catch (SQLException | ClassNotFoundException e) {
      sneakyThrow(e);
    }
  }

  @Override
  public int columnCount() {
    return columnsByIndex.size();
  }

  @Override
  public Iterable<String> columnNames() {
    return columnsByName.keySet();
  }

  @Override
  public Iterable<ColumnMetaData> allColumns() {
    return columnsByIndex.values();
  }

  @Override
  public Option<ColumnMetaData> column(String name) {
    return Option.of(columnsByName.get(name.toUpperCase()));
  }

  @Override
  public Option<ColumnMetaData> column(int index) {
    return Option.of(columnsByIndex.get(index));
  }

  private static ColumnMetaData createColumn(ResultSetMetaData impl, int i) throws SQLException, ClassNotFoundException {
    return new ColumnMetaData(
        impl.getColumnName(i),
        Class.forName(impl.getColumnClassName(i)),
        impl.isNullable(i) != ResultSetMetaData.columnNullableUnknown ?
            impl.isNullable(i) == ResultSetMetaData.columnNullable : null);
  }
}

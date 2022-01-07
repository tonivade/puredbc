/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.Precondition.checkNonNull;
import java.util.ArrayList;
import java.util.List;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.Nullability;
import io.r2dbc.spi.RowMetadata;

final class R2dbcRowMetaData implements RowMetaData {

  private final RowMetadata impl;

  protected R2dbcRowMetaData(RowMetadata impl) {
    this.impl = checkNonNull(impl);
  }

  @Override
  public int columnCount() {
    return impl.getColumnNames().size();
  }

  @Override
  public Iterable<String> columnNames() {
    return impl.getColumnNames();
  }

  @Override
  public Iterable<ColumnMetaData> allColumns() {
    List<ColumnMetaData> columns = new ArrayList<>();
    for (ColumnMetadata meta : impl.getColumnMetadatas()) {
      columns.add(createColumn(meta));
    }
    return ImmutableList.from(columns);
  }

  @Override
  public Option<ColumnMetaData> column(String name) {
    return Try.of(() -> impl.getColumnMetadata(name)).map(R2dbcRowMetaData::createColumn).toOption();
  }

  @Override
  public Option<ColumnMetaData> column(int index) {
    return Try.of(() -> impl.getColumnMetadata(index)).map(R2dbcRowMetaData::createColumn).toOption();
  }

  private static ColumnMetaData createColumn(ColumnMetadata meta) {
    return new ColumnMetaData(
        meta.getName(),
        meta.getJavaType(),
        meta.getNullability() != Nullability.UNKNOWN ? meta.getNullability() == Nullability.NULLABLE : null);
  }
}

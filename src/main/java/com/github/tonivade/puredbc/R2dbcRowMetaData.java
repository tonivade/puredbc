/*
 * Copyright (c) 2020-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.core.Precondition.checkNonNull;
import static com.github.tonivade.purefun.data.ImmutableList.toImmutableList;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.Nullability;
import io.r2dbc.spi.RowMetadata;

final class R2dbcRowMetaData implements RowMetaData {

  private final RowMetadata impl;

  R2dbcRowMetaData(RowMetadata impl) {
    this.impl = checkNonNull(impl);
  }

  @Override
  public int columnCount() {
    return impl.getColumnMetadatas().size();
  }

  @Override
  public Iterable<String> columnNames() {
    return impl.getColumnMetadatas().stream().map(ColumnMetadata::getName).collect(toImmutableList());
  }

  @Override
  public Iterable<ColumnMetaData> allColumns() {
    return impl.getColumnMetadatas().stream().map(R2dbcRowMetaData::createColumn).collect(toImmutableList());
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

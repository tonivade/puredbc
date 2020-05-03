/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import io.r2dbc.spi.RowMetadata;

import static java.util.Objects.requireNonNull;

public class R2dbcRowMetaData implements RowMetaData {

  private final RowMetadata impl;

  public R2dbcRowMetaData(RowMetadata impl) {
    this.impl = requireNonNull(impl);
  }

  @Override
  public int columnCount() {
    return impl.getColumnNames().size();
  }

  @Override
  public Iterable<String> columnNames() {
    return impl.getColumnNames();
  }
}

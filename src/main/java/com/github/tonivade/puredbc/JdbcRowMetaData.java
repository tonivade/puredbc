/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Producer;
import com.github.tonivade.purefun.data.ImmutableList;

import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class JdbcRowMetaData implements RowMetaData {

  private final ResultSetMetaData impl;

  public JdbcRowMetaData(ResultSetMetaData impl) {
    this.impl = requireNonNull(impl);
  }

  @Override
  public int columnCount() {
    return run(impl::getColumnCount);
  }

  @Override
  public Iterable<String> columnNames() {
    return run(() -> {
      List<String> list = new LinkedList<>();
      for (int i = 1; i <= impl.getColumnCount(); i++) {
        list.add(impl.getColumnName(i));
      }
      return ImmutableList.from(list);
    });
  }

  private static <T> T run(Producer<T> producer) {
    return producer.get();
  }
}

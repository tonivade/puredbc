/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.Precondition.checkNonNull;
import java.util.Objects;
import com.github.tonivade.purefun.Equal;
import com.github.tonivade.purefun.type.Option;

public interface RowMetaData {

  int columnCount();

  Iterable<String> columnNames();

  Iterable<ColumnMetaData> allColumns();

  Option<ColumnMetaData> column(String name);
  
  Option<ColumnMetaData> column(int index);

  final class ColumnMetaData {

    private static final Equal<ColumnMetaData> EQUAL = 
      Equal.<ColumnMetaData>of()
        .comparing(ColumnMetaData::name).comparing(ColumnMetaData::type).comparing(x -> x.nullable);

    private final String name;
    private final Class<?> type;
    private final Boolean nullable;

    protected ColumnMetaData(String name, Class<?> type, Boolean nullable) {
      this.name = checkNonNull(name);
      this.type = checkNonNull(type);
      this.nullable = nullable;
    }

    public String name() {
      return name;
    }

    public Class<?> type() {
      return type;
    }

    public Option<Boolean> isNullable() {
      return Option.of(nullable);
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(name, type, nullable);
    }
    
    @Override
    public boolean equals(Object obj) {
      return EQUAL.applyTo(this, obj);
    }

    @Override
    public String toString() {
      return String.format("ColumnMetaData [name=%s, type=%s]", name, type);
    }
  }
}

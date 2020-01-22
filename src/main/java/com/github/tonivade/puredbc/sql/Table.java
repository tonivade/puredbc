/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.data.NonEmptyList;

public interface Table {
  NonEmptyList<Field<?>> all();
  String name();
  String alias();
}

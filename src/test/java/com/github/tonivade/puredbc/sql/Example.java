/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.data.NonEmptyList;

import static java.util.Objects.requireNonNull;

final class Example implements Table {

  Field<String> field = Field.of(this, "name");
  Field<String> other = Field.of(this, "other");

  private final String name;

  Example() {
    this("example");
  }

  private Example(String name) {
    this.name = requireNonNull(name);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String alias() {
    return name;
  }

  @Override
  public NonEmptyList<Field<?>> all() {
    return NonEmptyList.of(field, other);
  }

  public Example as(String alias) {
    return new Example(alias);
  }
}

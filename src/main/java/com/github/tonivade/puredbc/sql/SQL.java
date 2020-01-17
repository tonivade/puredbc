/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.data.NonEmptyList;
import com.github.tonivade.purefun.data.Sequence;

import static com.github.tonivade.purefun.Function1.cons;
import static com.github.tonivade.purefun.data.ImmutableArray.empty;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class SQL {

  private final String query;
  private final Sequence<?> values;

  protected SQL(String query) {
    this(query, empty());
  }

  protected SQL(String query, Sequence<?> values) {
    this.query = requireNonNull(query);
    this.values = requireNonNull(values);
  }

  public String getQuery() {
    return query;
  }

  public Sequence<?> getParams() {
    return values;
  }

  @Override
  public String toString() {
    return String.format("SQL{query='%s', values=%s}", query, values);
  }

  public SQL from(Table table) {
    return sql(query + " from " + table.name());
  }

  public <T> SQL1<T> where(Condition<T> condition) {
    return new SQL1<>(query + " where " + condition);
  }

  public <A> SQL1<A> set(Field<A> f1) {
    return new SQL1<>(query + set(arrayOf(f1)));
  }

  public <A, B> SQL2<A, B> set(Field<A> a, Field<B> b) {
    return new SQL2<>(query + set(arrayOf(a, b)));
  }

  public <A, B, C> SQL3<A, B, C> set(Field<A> a, Field<B> b, Field<C> c) {
    return new SQL3<>(query + set(arrayOf(a, b, c)));
  }

  public <A, B, C, D> SQL4<A, B, C, D> set(Field<A> a, Field<B> b, Field<C> c, Field<D> d) {
    return new SQL4<>(query + set(arrayOf(a, b, c, d)));
  }

  public <A, B, C, D, E> SQL5<A, B, C, D, E> set(Field<A> a, Field<B> b, Field<C> c, Field<D> d, Field<E> e) {
    return new SQL5<>(query + set(arrayOf(a, b, c, d, e)));
  }

  public <A> SQL1<A> values(Field<A> f1) {
    return new SQL1<>(query + values(arrayOf(f1)));
  }

  public <A, B> SQL2<A, B> values(Field<A> a, Field<B> b) {
    return new SQL2<>(query + values(arrayOf(a, b)));
  }

  public <A, B, C> SQL3<A, B, C> values(Field<A> a, Field<B> b, Field<C> c) {
    return new SQL3<>(query + values(arrayOf(a, b, c)));
  }

  public <A, B, C, D> SQL4<A, B, C, D> values(Field<A> a, Field<B> b, Field<C> c, Field<D> d) {
    return new SQL4<>(query + values(arrayOf(a, b, c, d)));
  }

  public <A, B, C, D, E> SQL5<A, B, C, D, E> values(Field<A> a, Field<B> b, Field<C> c, Field<D> d, Field<E> e) {
    return new SQL5<>(query + values(arrayOf(a, b, c, d, e)));
  }

  public static SQL sql(String line, String... lines) {
    return new SQL(arrayOf(line).appendAll(arrayOf(lines)).join(" "));
  }

  public static SQL select(Field<?> field, Field<?>... fields) {
    return select(NonEmptyList.of(field, fields));
  }

  public static SQL select(NonEmptyList<Field<?>> fields) {
    return sql(fields.map(Field::name).join(",", "select ", " "));
  }

  public static SQL insert(Table table) {
    return sql("insert into " + table.name());
  }

  public static SQL update(Table table) {
    return sql("update " + table.name());
  }

  public static SQL delete(Table table) {
    return sql("delete from " + table.name());
  }

  private String values(Sequence<Field<?>> values) {
    return values.map(Field::name).join(",", " (", values.map(cons("?")).join(",", ") values (", ")"));
  }

  private String set(Sequence<Field<?>> values) {
    return values.map(field -> field.name() + "=?").join(",", " set ", "");
  }
}

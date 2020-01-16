/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.data.Sequence;

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
    return sql(query + " from " + table);
  }

  public <T> SQL1<T> where(Condition<T> condition) {
    return new SQL1<>(query + " where " + condition);
  }

  public <A> SQL1<A> set(Field<A> f1) {
    return new SQL1<>(query + " set " + f1 + "=?");
  }

  public <A, B> SQL2<A, B> set(Field<A> f1, Field<B> f2) {
    return new SQL2<>(query + " set " + f1 + "=?," + f2 + "=?");
  }

  public <A, B, C> SQL3<A, B, C> set(Field<A> f1, Field<B> f2, Field<C> f3) {
    return new SQL3<>(query + " set " + f1 + " = ?," + f2 + " = ?," + f3 + " = ?");
  }

  public <A, B, C, D> SQL4<A, B, C, D> set(Field<A> f1, Field<B> f2, Field<C> f3, Field<D> f4) {
    return new SQL4<>(query + " set " + f1 + "=?," + f2 + "=?," + f3 + "=?," + f4 + "=?");
  }

  public <A, B, C, D, E> SQL5<A, B, C, D, E> set(Field<A> f1, Field<B> f2, Field<C> f3, Field<D> f4, Field<E> f5) {
    return new SQL5<>(query + " set " + f1 + "=?," + f2 + "=?," + f3 + "=?," + f4 + "=?," + f5 + "=?");
  }

  public <A, B, C, D, E, F> SQL6<A, B, C, D, E, F> set(Field<A> f1, Field<B> f2, Field<C> f3, Field<D> f4, Field<E> f5, Field<F> f6) {
    return new SQL6<>(query + " set " + f1 + "=?," + f2 + "=?," + f3 + "=?," + f4 + "=?," + f5 + "=?," + f6 + "=?");
  }

  public <A> SQL1<A> values(Field<A> f1) {
    return new SQL1<>(query + " (" + f1 + ") values (?)");
  }

  public <A, B> SQL2<A, B> values(Field<A> f1, Field<B> f2) {
    return new SQL2<>(query + " (" + f1 + "," + f2 + ") values (?,?)");
  }

  public <A, B, C> SQL3<A, B, C> values(Field<A> f1, Field<B> f2, Field<C> f3) {
    return new SQL3<>(query + " (" + f1 + "," + f2 + "," + f3 + ") values (?,?,?)");
  }

  public <A, B, C, D> SQL4<A, B, C, D> values(Field<A> f1, Field<B> f2, Field<C> f3, Field<D> f4) {
    return new SQL4<>(query + " (" + f1 + "," + f2 + "," + f3+ "," + f4 + ") values (?,?,?,?)");
  }

  public <A, B, C, D, E> SQL5<A, B, C, D, E> values(Field<A> f1, Field<B> f2, Field<C> f3, Field<D> f4, Field<E> f5) {
    return new SQL5<>(query + " (" + f1 + "," + f2 + "," + f3 + "," + f4 + "," + f5 + ") values (?,?,?,?,?)");
  }

  public <A, B, C, D, E, F> SQL6<A, B, C, D, E, F> values(Field<A> f1, Field<B> f2, Field<C> f3, Field<D> f4, Field<E> f5, Field<F> f6) {
    return new SQL6<>(query + " (" + f1 + "," + f2 + "," + f3 + "," + f4 + "," + f5 + "," + f6 + ") values (?,?,?,?,?,?)");
  }

  public static SQL sql(String query, String... append) {
    return new SQL(arrayOf(query).appendAll(arrayOf(append)).join(" "));
  }

  public static SQL select(Field<?>... fields) {
    return sql(arrayOf(fields).join(",", "select ", " "));
  }

  public static SQL insert(Table table) {
    return sql("insert into " + table);
  }

  public static SQL update(Table table) {
    return sql("update " + table);
  }

  public static SQL delete(Table table) {
    return sql("delete from " + table);
  }
}

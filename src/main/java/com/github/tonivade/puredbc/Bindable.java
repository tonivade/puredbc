/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.data.Sequence;

import static com.github.tonivade.purefun.data.ImmutableArray.empty;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

public final class Bindable {

  private final String query;
  private final Sequence<?> values;

  protected Bindable(String query) {
    this(query, empty());
  }

  protected Bindable(String query, Sequence<?> values) {
    this.query = requireNonNull(query);
    this.values = requireNonNull(values);
  }

  String getQuery() {
    return query;
  }

  Sequence<?> getParams() {
    return values;
  }

  @Override
  public String toString() {
    return "Bindable{" +
        "query='" + query + '\'' +
        ", values=" + values +
        '}';
  }

  public Bindable from(String table) {
    return sql(query + " from " + table);
  }

  public <T> Bindable1<T> where(String condition) {
    return new Bindable1<>(query + " where " + condition);
  }

  public <A> Bindable1<A> set(String f1) {
    return new Bindable1<>(query + " set " + f1 + "=?");
  }

  public <A, B> Bindable2<A, B> set(String f1, String f2) {
    return new Bindable2<>(query + " set " + f1 + "=?," + f2 + "=?");
  }

  public <A, B, C> Bindable3<A, B, C> set(String f1, String f2, String f3) {
    return new Bindable3<>(query + " set " + f1 + " = ?," + f2 + " = ?," + f3 + " = ?");
  }

  public <A, B, C, D> Bindable4<A, B, C, D> set(String f1, String f2, String f3, String f4) {
    return new Bindable4<>(query + " set " + f1 + "=?," + f2 + "=?," + f3 + "=?," + f4 + "=?");
  }

  public <A> Bindable1<A> values(String f1) {
    return new Bindable1<>(query + " (" + f1 + ") values (?)");
  }

  public <A, B> Bindable2<A, B> values(String f1, String f2) {
    return new Bindable2<>(query + " (" + f1 + "," + f2 + ") values (?,?)");
  }

  public <A, B, C> Bindable3<A, B, C> values(String f1, String f2, String f3) {
    return new Bindable3<>(query + " (" + f1 + "," + f2 + "," + f3 + ") values (?,?,?)");
  }

  public <A, B, C, D> Bindable4<A, B, C, D> values(String f1, String f2, String f3, String f4) {
    return new Bindable4<>(query + " (" + f1 + "," + f2 + "," + f3+ "," + f4 + ") values (?,?,?,?)");
  }

  public <A, B, C, D, E> Bindable5<A, B, C, D, E> values(String f1, String f2, String f3, String f4, String f5) {
    return new Bindable5<>(query + " (" + f1 + "," + f2 + "," + f3 + "," + f4 + "," + f5 + ") values (?,?,?,?,?)");
  }

  public static Bindable sql(String query) {
    return new Bindable(query);
  }

  public static Bindable select(String... fields) {
    return sql(arrayOf(fields).join(",", "select ", " "));
  }

  public static Bindable insertInto(String table) {
    return sql("insert into " + table);
  }

  public static Bindable update(String table) {
    return sql("update " + table);
  }

  public static Bindable deleteFrom(String table) {
    return sql("delete from " + table);
  }
}

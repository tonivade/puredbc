/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import com.github.tonivade.purefun.data.NonEmptyList;
import com.github.tonivade.purefun.data.Range;
import com.github.tonivade.purefun.data.Sequence;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.tonivade.purefun.Function1.cons;
import static com.github.tonivade.purefun.data.ImmutableArray.empty;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static com.github.tonivade.purefun.data.Sequence.interleave;
import static java.util.stream.Collectors.joining;

public final class SQL {

  private final String query;
  private final Sequence<?> values;

  protected SQL(String query) {
    this(query, empty());
  }

  protected SQL(String query, Sequence<?> values) {
    this.query = SQLModule.process(query, values);
    this.values = values;
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

  public SQL from(SQL other) {
    return sql(query + " from (" + other.getQuery() + ")");
  }

  public SQL1 from(SQL1 other) {
    return other.from(this);
  }

  public SQL from(Table table, Table... tables) {
    return sql(query + " from " + NonEmptyList.of(table, tables).map(Table::name).join(","));
  }

  public SQL innerJoin(Table table) {
    return sql(query + " inner join " + table.name());
  }

  public SQL leftJoin(Table table) {
    return sql(query + " left join " + table.name());
  }

  public SQL rightJoin(Table table) {
    return sql(query + " right join " + table.name());
  }

  public SQL fullJoin(Table table) {
    return sql(query + " full join " + table.name());
  }

  public <T> SQL on(Field<T> from, Field<T> to) {
    return sql(query + " on " + from.name() + " = " + to.name());
  }

  public <T> SQL1<T> where(Condition<T> condition) {
    return new SQL1<>(query + " where " + condition.expression());
  }

  public SQL limit(int limit) {
    return sql(query + " limit " + limit);
  }

  public SQL offset(int offset) {
    return sql(query + " offset " + offset);
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
    return sql(fields.map(Field::name).join(", ", "select ", ""));
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
    String suffix = values.map(cons("?")).join(", ", ") values (", ")");
    return values.map(Field::name).join(", ", " (", suffix);
  }

  private String set(Sequence<Field<?>> values) {
    return values.map(field -> field.name() + " = ?").join(",", " set ", "");
  }
}

interface SQLModule {
  static String process(String query, Sequence<?> values) {
    if (values.isEmpty()) return query;
    Stream<String> split = Pattern.compile("\\?").splitAsStream(query);
    Stream<String> replacements = values.stream().map(SQLModule::replacement);
    return interleave(split, replacements).collect(joining());
  }

  static String replacement(Object value) {
    if (value instanceof Range) { // between
      return "? and ?";
    }
    if (value instanceof Sequence) { // in
      return ((Sequence<?>) value).map(cons("?")).join(", ");
    }
    return "?";
  }
}

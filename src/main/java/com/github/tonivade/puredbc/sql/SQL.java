/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

import static com.github.tonivade.purefun.Function1.cons;
import static com.github.tonivade.purefun.Precondition.checkNonEmpty;
import static com.github.tonivade.purefun.Precondition.checkNonNull;
import static com.github.tonivade.purefun.data.ImmutableArray.empty;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static com.github.tonivade.purefun.data.Sequence.interleave;
import static java.util.stream.Collectors.joining;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.data.ImmutableArray;
import com.github.tonivade.purefun.data.NonEmptyList;
import com.github.tonivade.purefun.data.Range;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.typeclasses.TupleK;

public final class SQL {

  private final String query;
  private final Sequence<?> values;

  protected SQL(String query) {
    this(query, empty());
  }

  protected SQL(String query, Sequence<?> values) {
    this.query = process(query, values);
    this.values = checkNonNull(values);
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

  public SQL from(Table<?, ?> table, Table<?, ?>... tables) {
    return sql(query + " from " + NonEmptyList.of(table, tables).map(Table::name).join(","));
  }

  public <T extends Tuple, F extends TupleK<Field_>> SQL innerJoin(Table<T, F> table) {
    return sql(query + " inner join " + table.name());
  }

  public <T extends Tuple, F extends TupleK<Field_>> SQL leftJoin(Table<T, F> table) {
    return sql(query + " left join " + table.name());
  }

  public <T extends Tuple, F extends TupleK<Field_>> SQL rightJoin(Table<T, F> table) {
    return sql(query + " right join " + table.name());
  }

  public <T extends Tuple, F extends TupleK<Field_>> SQL fullJoin(Table<T, F> table) {
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
  
  public static <T extends Tuple, F extends TupleK<Field_>> SQL selectFrom(Table<T, F> table) {
    return select(table.all()).from(table);
  }

  public static <T extends Tuple, F extends TupleK<Field_>> SQL insertInto(Table<T, F> table) {
    return sql("insert into " + table.name());
  }

  public static <T extends Tuple, F extends TupleK<Field_>> SQL update(Table<T, F> table) {
    return sql("update " + table.name());
  }

  public static <T extends Tuple, F extends TupleK<Field_>> SQL deleteFrom(Table<T, F> table) {
    return sql("delete from " + table.name());
  }

  private static String values(Sequence<Field<?>> values) {
    String suffix = values.map(cons("?")).join(", ", ") values (", ")");
    return values.map(Field::name).join(", ", " (", suffix);
  }

  private static String set(Sequence<Field<?>> values) {
    return values.map(field -> field.name() + " = ?").join(",", " set ", "");
  }
  
  private static String process(String query, Sequence<?> values) {
    checkNonEmpty(query);
    checkNonNull(values);
    if (values.isEmpty()) return query;
    Stream<String> split = Pattern.compile("\\?").splitAsStream(query);
    Stream<String> replacements = values.stream().map(SQL::replacement);
    return interleave(split, replacements).collect(joining());
  }

  private static String replacement(Object value) {
    if (value instanceof Range) { // between
      return "? and ?";
    }
    if (value instanceof Iterable) { // in
      return ImmutableArray.from((Iterable<?>) value).map(cons("?")).join(", ");
    }
    return "?";
  }
}

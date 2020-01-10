/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Higher1;
import com.github.tonivade.purefun.HigherKind;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.free.Free;
import com.github.tonivade.purefun.instances.IdInstances;
import com.github.tonivade.purefun.type.Id;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.typeclasses.Transformer;

import javax.sql.DataSource;
import java.sql.ResultSet;

import static com.github.tonivade.puredbc.JdbcTemplate.iterable;
import static com.github.tonivade.puredbc.JdbcTemplate.option;
import static java.util.Objects.requireNonNull;

@HigherKind
public interface DSL<T> {

  static <A> Function1<DataSource, A> run(Free<DSL.µ, A> free) {
    return dataSource -> {
      JdbcTemplate jdbc = new JdbcTemplate(dataSource);
      Higher1<Id.µ, A> foldMap = free.foldMap(IdInstances.monad(), new Transformer<DSL.µ, Id.µ>() {
        @Override
        public <T> Higher1<Id.µ, T> apply(Higher1<DSL.µ, T> from) {
          DSL<T> dsl = DSL.narrowK(from);
          if (dsl instanceof DSL.Update) {
            DSL.Update update = (DSL.Update) dsl;
            return (Higher1<Id.µ, T>) Id.of(jdbc.update(update.getQuery(), update.getParams())).kind1();
          }
          if (dsl instanceof DSL.Query) {
            DSL.Query<T> query = (DSL.Query<T>) dsl;
            return (Higher1<Id.µ, T>) Id.of(jdbc.query(query.getQuery(), query.getParams(), query.getExtractor())).kind1();
          }
          throw new IllegalStateException();
        }
      });
      return foldMap.fix1(Id::narrowK).get();
    };
  }

  static Free<DSL.µ, Unit> update(Bindable query) {
    return Free.liftF(new Update(query).kind1());
  }

  static Free<DSL.µ, Iterable<Integer>> queryForInt(Bindable query) {
    return query(query, rs -> rs.getInt(1));
  }

  static Free<DSL.µ, Iterable<Long>> queryForLong(Bindable query) {
    return query(query, rs -> rs.getLong(1));
  }

  static <T> Free<DSL.µ, Option<T>> queryOne(Bindable query, Function1<ResultSet, T> rowMapper) {
    return Free.liftF(new Query<>(query, option(rowMapper)).kind1());
  }

  static <T> Free<DSL.µ, Iterable<T>> query(Bindable query, Function1<ResultSet, T> rowMapper) {
    return Free.liftF(new Query<>(query, iterable(rowMapper)).kind1());
  }

  final class Query<T> implements DSL<T> {

    private final Bindable query;
    private final Function1<ResultSet, T> extractor;

    public Query(Bindable query, Function1<ResultSet, T> extractor) {
      this.query = requireNonNull(query);
      this.extractor = requireNonNull(extractor);
    }

    public String getQuery() {
      return query.getQuery();
    }

    public Sequence<Object> getParams() {
      return query.getParams();
    }

    public Function1<ResultSet, T> getExtractor() {
      return extractor;
    }

    @Override
    public String toString() {
      return "Query{" +
          "query=" + query +
          '}';
    }
  }

  final class Update implements DSL<Unit> {

    private final Bindable query;

    public Update(Bindable query) {
      this.query = requireNonNull(query);
    }

    public String getQuery() {
      return query.getQuery();
    }

    public Sequence<Object> getParams() {
      return query.getParams();
    }

    @Override
    public String toString() {
      return "Update{" +
          "query=" + query +
          '}';
    }
  }
}

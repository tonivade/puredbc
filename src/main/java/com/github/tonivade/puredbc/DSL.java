/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Higher1;
import com.github.tonivade.purefun.HigherKind;
import com.github.tonivade.purefun.Recoverable;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.free.Free;
import com.github.tonivade.purefun.instances.IdInstances;
import com.github.tonivade.purefun.type.Id;
import com.github.tonivade.purefun.typeclasses.Transformer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.github.tonivade.purefun.Unit.unit;
import static com.github.tonivade.purefun.data.ImmutableList.empty;
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

  static Free<DSL.µ, Unit> update(String query) {
    return update(query, empty());
  }

  static Free<DSL.µ, Unit> update(String query, Sequence<?> params) {
    return Free.liftF(new Update(query, params).kind1());
  }

  static Free<DSL.µ, Iterable<Integer>> queryForInt(String query) {
    return queryForInt(query, empty());
  }

  static Free<DSL.µ, Iterable<Integer>> queryForInt(String query, Sequence<?> params) {
    return query(query, params, rs -> rs.getInt(1));
  }

  static Free<DSL.µ, Iterable<Long>> queryForLong(String query) {
    return queryForLong(query, empty());
  }

  static Free<DSL.µ, Iterable<Long>> queryForLong(String query, Sequence<?> params) {
    return query(query, params, rs -> rs.getLong(1));
  }

  static <T> Free<DSL.µ, Iterable<T>> query(String query, Function1<ResultSet, T> extractor) {
    return Free.liftF(new Query<>(query, empty(), extractor).kind1());
  }

  static <T> Free<DSL.µ, Iterable<T>> query(String query, Sequence<?> params, Function1<ResultSet, T> extractor) {
    return Free.liftF(new Query<>(query, params, extractor).kind1());
  }

  final class Query<T> implements DSL<Iterable<T>> {

    private final String query;
    private final Sequence<?> params;
    private final Function1<ResultSet, T> extractor;

    public Query(String query, Sequence<?> params, Function1<ResultSet, T> extractor) {
      this.query = requireNonNull(query);
      this.params = requireNonNull(params);
      this.extractor = requireNonNull(extractor);
    }

    public String getQuery() {
      return query;
    }

    public Sequence<?> getParams() {
      return params;
    }

    public Function1<ResultSet, T> getExtractor() {
      return extractor;
    }

    @Override
    public String toString() {
      return "Query{" +
          "query='" + query + '\'' +
          ", params=" + params +
          '}';
    }
  }

  final class Update implements DSL<Unit> {

    private final String query;
    private final Sequence<?> params;

    public Update(String query, Sequence<?> params) {
      this.query = requireNonNull(query);
      this.params = requireNonNull(params);
    }

    public String getQuery() {
      return query;
    }

    public Sequence<?> getParams() {
      return params;
    }

    @Override
    public String toString() {
      return "Update{" +
          "query='" + query + '\'' +
          ", params=" + params +
          '}';
    }
  }
}

class JdbcTemplate implements Recoverable {

  public final DataSource dataSource;

  public JdbcTemplate(DataSource dataSource) {
    this.dataSource = requireNonNull(dataSource);
  }

  Unit update(String query, Sequence<?> params) {
    try (Connection conn = dataSource.getConnection()) {
      PreparedStatement preparedStatement = conn.prepareStatement(query);
      int i = 1;
      for (Object param : params) {
        preparedStatement.setObject(i++, param);
      }
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      return sneakyThrow(e);
    }
    return unit();
  }

  <T> Iterable<T> query(String query, Sequence<?> params, Function1<ResultSet, T> extractor) {
    try (Connection conn = dataSource.getConnection()) {
      PreparedStatement preparedStatement = conn.prepareStatement(query);
      int i = 1;
      for (Object param : params) {
        preparedStatement.setObject(i++, param);
      }
      List<T> result = new ArrayList<>();
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        result.add(extractor.apply(resultSet));
      }
      return ImmutableList.from(result);
    } catch (SQLException e) {
      return sneakyThrow(e);
    }
  }
}

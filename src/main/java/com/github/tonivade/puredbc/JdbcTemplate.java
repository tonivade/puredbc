/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Consumer1;
import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Recoverable;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.type.Option;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.github.tonivade.purefun.Function1.cons;
import static com.github.tonivade.purefun.Unit.unit;
import static java.util.Objects.requireNonNull;

class JdbcTemplate implements Recoverable {

  public final DataSource dataSource;

  public JdbcTemplate(DataSource dataSource) {
    this.dataSource = requireNonNull(dataSource);
  }

  public Unit update(String query, Sequence<?> params) {
    return _update(query, populateWith(params), cons(unit()));
  }

  public <T> Option<T> updateWithKeys(String query, Sequence<?> params, Function1<ResultSet, T> rowMapper) {
    return _update(query, populateWith(params), optionExtractor(rowMapper));
  }

  public <T> T query(String query, Sequence<?> params, Function1<ResultSet, T> rowMapper) {
    return _query(query, populateWith(params), rowMapper);
  }

  public <T> Option<T> queryOne(String query, Sequence<?> params, Function1<ResultSet, T> rowMapper) {
    return _query(query, populateWith(params), optionExtractor(rowMapper));
  }

  public <T> Iterable<T> queryIterable(String query, Sequence<?> params, Function1<ResultSet, T> rowMapper) {
    return _query(query, populateWith(params), iterableExtractor(rowMapper));
  }

  private <T> T _query(String query, Consumer1<PreparedStatement> setter, Function1<ResultSet, T> extractor) {
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(query)) {
        setter.accept(stmt);
        return extractor.apply(stmt.executeQuery());
      }
    } catch (SQLException e) {
      return sneakyThrow(e);
    }
  }

  private <T> T _update(String query, Consumer1<PreparedStatement> setter, Function1<ResultSet, T> extractor) {
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        setter.accept(stmt);
        stmt.executeUpdate();
        try (ResultSet rs = stmt.getGeneratedKeys()) {
          return extractor.apply(rs);
        }
      }
    } catch (SQLException e) {
      return sneakyThrow(e);
    }
  }

  private static <T> Function1<ResultSet, Option<T>> optionExtractor(Function1<ResultSet, T> rowMapper) {
    return rs -> {
      if (rs.next()) {
        return Option.some(rowMapper.apply(rs));
      }
      return Option.none();
    };
  }

  private static <T> Function1<ResultSet, Iterable<T>> iterableExtractor(Function1<ResultSet, T> rowMapper) {
    return rs -> {
      List<T> result = new ArrayList<>();
      while (rs.next()) {
        result.add(rowMapper.apply(rs));
      }
      return ImmutableList.from(result);
    };
  }

  private static Consumer1<PreparedStatement> populateWith(Sequence<?> params) {
    return stmt -> {
      int i = 1;
      for (Object param : params) {
        stmt.setObject(i++, param);
      }
    };
  }
}
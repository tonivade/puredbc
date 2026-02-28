/*
 * Copyright (c) 2020-2026, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.core.Function1.cons;
import static com.github.tonivade.purefun.core.Precondition.checkNonNull;
import static com.github.tonivade.purefun.core.Unit.unit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.purefun.core.Consumer1;
import com.github.tonivade.purefun.core.Function1;
import com.github.tonivade.purefun.core.Recoverable;
import com.github.tonivade.purefun.core.Unit;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.Range;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.purefun.type.Option;

public class JdbcTemplate implements Recoverable, AutoCloseable {

  public final Connection conn;

  public JdbcTemplate(Connection conn) {
    this.conn = checkNonNull(conn);
  }

  public Unit update(String query, Sequence<?> params) {
    return doUpdate(query, populateWith(params), cons(unit()));
  }

  public <T> Option<T> updateWithKeys(String query, Sequence<?> params, Field<T> field) {
    return doUpdate(query, populateWith(params), optionExtractor(getField(field).compose(JdbcRow::new)));
  }

  public <T> Option<T> queryMeta(String query, Sequence<?> params, Function1<RowMetaData, T> rowMapper) {
    return doQuery(query, populateWith(params),
        optionExtractor(rowMapper.compose(rs -> new JdbcRowMetaData(rs.getMetaData()))));
  }

  public <T> Option<T> queryOne(String query, Sequence<?> params, Function1<Row, T> rowMapper) {
    return doQuery(query, populateWith(params), optionExtractor(rowMapper.compose(JdbcRow::new)));
  }

  public <T> Iterable<T> queryIterable(String query, Sequence<?> params, Function1<Row, T> rowMapper) {
    return doQuery(query, populateWith(params), iterableExtractor(rowMapper.compose(JdbcRow::new)));
  }

  @Override
  public void close() throws Exception {
    conn.close();
  }

  private <T> T doQuery(String query, Consumer1<PreparedStatement> setter, Function1<ResultSet, T> extractor) {
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      setter.accept(stmt);
      try (ResultSet rs = stmt.executeQuery()) {
        return extractor.apply(rs);
      }
    } catch (SQLException e) {
      return sneakyThrow(e);
    }
  }

  private <T> T doUpdate(String query, Consumer1<PreparedStatement> setter, Function1<ResultSet, T> extractor) {
    try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      setter.accept(stmt);
      stmt.executeUpdate();
      try (ResultSet rs = stmt.getGeneratedKeys()) {
        return extractor.apply(rs);
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
      for (var param : params) {
        switch (param) {
          case Range(var begin, var end, var increment) when increment == 1 -> {
            stmt.setObject(i++, begin);
            stmt.setObject(i++, end);
          }
          case Iterable<?> iterable -> {
            for (var p : iterable) {
              stmt.setObject(i++, p);
            }
          }
          case null, default -> stmt.setObject(i++, param);
        }
      }
    };
  }

  private static <T> Function1<Row, T> getField(Field<T> field) {
    return row -> row.get(field);
  }
}
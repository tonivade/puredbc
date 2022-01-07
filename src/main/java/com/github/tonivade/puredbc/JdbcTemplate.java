/*
 * Copyright (c) 2020-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.Function1.cons;
import static com.github.tonivade.purefun.Precondition.checkNonNull;
import static com.github.tonivade.purefun.Unit.unit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.purefun.Consumer1;
import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Recoverable;
import com.github.tonivade.purefun.Unit;
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
    return _update(query, populateWith(params), cons(unit()));
  }

  public <T> Option<T> updateWithKeys(String query, Sequence<?> params, Field<T> field) {
    return _update(query, populateWith(params), optionExtractor(getField(field).compose(JdbcRow::new)));
  }

  public <T> Option<T> queryMeta(String query, Sequence<?> params, Function1<RowMetaData, T> rowMapper) {
    return _query(query, populateWith(params),
        optionExtractor(rowMapper.compose(rs -> new JdbcRowMetaData(rs.getMetaData()))));
  }

  public <T> Option<T> queryOne(String query, Sequence<?> params, Function1<Row, T> rowMapper) {
    return _query(query, populateWith(params), optionExtractor(rowMapper.compose(JdbcRow::new)));
  }

  public <T> Iterable<T> queryIterable(String query, Sequence<?> params, Function1<Row, T> rowMapper) {
    return _query(query, populateWith(params), iterableExtractor(rowMapper.compose(JdbcRow::new)));
  }

  @Override
  public void close() throws Exception {
    conn.close();
  }

  private <T> T _query(String query, Consumer1<PreparedStatement> setter, Function1<ResultSet, T> extractor) {
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      setter.accept(stmt);
      try (ResultSet rs = stmt.executeQuery()) {
        return extractor.apply(rs);
      }
    } catch (SQLException e) {
      return sneakyThrow(e);
    }
  }

  private <T> T _update(String query, Consumer1<PreparedStatement> setter, Function1<ResultSet, T> extractor) {
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
      for (Object param : params) {
        if (param instanceof Range) {
          var range = (Range) param;
          stmt.setObject(i++, range.begin());
          stmt.setObject(i++, range.end());
        } else if (param instanceof Iterable) {
          for (Object p : (Iterable<?>) param) {
            stmt.setObject(i++, p);
          }
        } else {
          stmt.setObject(i++, param);
        }
      }
    };
  }

  private static <T> Function1<Row, T> getField(Field<T> field) {
    return row -> row.get(field);
  }
}
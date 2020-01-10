/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Recoverable;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.data.Sequence;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.tonivade.purefun.Unit.unit;
import static java.util.Objects.requireNonNull;

class JdbcTemplate implements Recoverable {

  public final DataSource dataSource;

  public JdbcTemplate(DataSource dataSource) {
    this.dataSource = requireNonNull(dataSource);
  }

  public Unit update(String query, Sequence<?> params) {
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

  public <T> T query(String query, Sequence<?> params, Function1<ResultSet, T> extractor) {
    try (Connection conn = dataSource.getConnection()) {
      PreparedStatement preparedStatement = conn.prepareStatement(query);
      int i = 1;
      for (Object param : params) {
        preparedStatement.setObject(i++, param);
      }
      return extractor.apply(preparedStatement.executeQuery());
    } catch (SQLException e) {
      return sneakyThrow(e);
    }
  }
}
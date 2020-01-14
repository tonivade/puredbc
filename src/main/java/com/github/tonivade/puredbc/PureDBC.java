/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Higher1;
import com.github.tonivade.purefun.HigherKind;
import com.github.tonivade.purefun.Instance;
import com.github.tonivade.purefun.Kind;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.concurrent.Future;
import com.github.tonivade.purefun.effect.Task;
import com.github.tonivade.purefun.effect.UIO;
import com.github.tonivade.purefun.free.Free;
import com.github.tonivade.purefun.instances.FutureInstances;
import com.github.tonivade.purefun.instances.IdInstances;
import com.github.tonivade.purefun.instances.TaskInstances;
import com.github.tonivade.purefun.instances.TryInstances;
import com.github.tonivade.purefun.instances.UIOInstances;
import com.github.tonivade.purefun.type.Id;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.purefun.typeclasses.Monad;
import com.github.tonivade.purefun.typeclasses.FunctionK;

import javax.sql.DataSource;
import java.sql.ResultSet;

import static com.github.tonivade.purefun.Function1.cons;
import static java.util.Objects.requireNonNull;

@HigherKind
public final class PureDBC<T>  {

  private final Free<DSL.µ, T> value;

  private PureDBC(T value) {
    this(Free.pure(value));
  }

  private PureDBC(DSL<T> value) {
    this(Free.liftF(value.kind1()));
  }

  private PureDBC(Free<DSL.µ, T> value) {
    this.value = requireNonNull(value);
  }

  public <R> PureDBC<R> map(Function1<T, R> map) {
    return new PureDBC<>(value.map(map));
  }

  public <R> PureDBC<R> flatMap(Function1<T, PureDBC<R>> map) {
    return new PureDBC<>(value.flatMap(t -> map.apply(t).value));
  }

  public <R> PureDBC<R> andThen(PureDBC<R> next) {
    return flatMap(cons(next));
  }

  public T unsafeRun(DataSource dataSource) {
    return unsafeRun(value).compose(JdbcTemplate::new).apply(dataSource);
  }

  public Try<T> safeRun(DataSource dataSource) {
    return safeRun(value).compose(JdbcTemplate::new).apply(dataSource);
  }

  public UIO<T> unsafeRunIO(DataSource dataSource) {
    return unsafeRunIO(value).compose(JdbcTemplate::new).apply(dataSource);
  }

  public Task<T> safeRunIO(DataSource dataSource) {
    return safeRunIO(value).compose(JdbcTemplate::new).apply(dataSource);
  }

  public Future<T> asyncRun(DataSource dataSource) {
    return asyncRun(value).compose(JdbcTemplate::new).apply(dataSource);
  }

  public static <T> PureDBC<T> pure(T value) {
    return new PureDBC<>(value);
  }

  public static PureDBC<Unit> update(SQL query) {
    return new PureDBC<>(new DSL.Update(query));
  }

  public static <T> PureDBC<Option<T>> updateWithKeys(SQL query, Function1<ResultSet, T> extractor) {
    return new PureDBC<>(new DSL.UpdateWithKeys<>(query, extractor));
  }

  public static <T> PureDBC<T> query(SQL query, Function1<ResultSet, T> rowMapper) {
    return new PureDBC<>(new DSL.Query<>(query, rowMapper));
  }

  public static <T> PureDBC<Option<T>> queryOne(SQL query, Function1<ResultSet, T> rowMapper) {
    return new PureDBC<>(new DSL.QueryOne<>(query, rowMapper));
  }

  public static <T> PureDBC<Iterable<T>> queryIterable(SQL query, Function1<ResultSet, T> rowMapper) {
    return new PureDBC<>(new DSL.QueryIterable<>(query, rowMapper));
  }

  public static Monad<PureDBC.µ> monad() {
    return PureDBCMonad.instance();
  }

  private static <A> Function1<JdbcTemplate, A> unsafeRun(Free<DSL.µ, A> free) {
    return jdbc -> {
      DSLIdVisitor visitor = new DSLIdVisitor(jdbc);
      Higher1<Id.µ, A> foldMap = free.foldMap(IdInstances.monad(), new DSLTransformer<>(visitor));
      return foldMap.fix1(Id::narrowK).get();
    };
  }

  private static <A> Function1<JdbcTemplate, Try<A>> safeRun(Free<DSL.µ, A> free) {
    return jdbc -> {
      DSLTryVisitor visitor = new DSLTryVisitor(jdbc);
      Higher1<Try.µ, A> foldMap = free.foldMap(TryInstances.monad(), new DSLTransformer<>(visitor));
      return foldMap.fix1(Try::narrowK);
    };
  }

  private static <A> Function1<JdbcTemplate, UIO<A>> unsafeRunIO(Free<DSL.µ, A> free) {
    return jdbc -> {
      DSLUIOVisitor visitor = new DSLUIOVisitor(jdbc);
      Higher1<UIO.µ, A> foldMap = free.foldMap(UIOInstances.monad(), new DSLTransformer<>(visitor));
      return foldMap.fix1(UIO::narrowK);
    };
  }

  private static <A> Function1<JdbcTemplate, Task<A>> safeRunIO(Free<DSL.µ, A> free) {
    return jdbc -> {
      DSLTaskVisitor visitor = new DSLTaskVisitor(jdbc);
      Higher1<Task.µ, A> foldMap = free.foldMap(TaskInstances.monad(), new DSLTransformer<>(visitor));
      return foldMap.fix1(Task::narrowK);
    };
  }

  private static <A> Function1<JdbcTemplate, Future<A>> asyncRun(Free<DSL.µ, A> free) {
    return jdbc -> {
      DSLFutureVisitor visitor = new DSLFutureVisitor(jdbc);
      Higher1<Future.µ, A> foldMap = free.foldMap(FutureInstances.monad(), new DSLTransformer<>(visitor));
      return foldMap.fix1(Future::narrowK);
    };
  }

  private static class DSLIdVisitor implements DSL.Visitor<Id.µ> {

    private final JdbcTemplate jdbc;

    public DSLIdVisitor(JdbcTemplate jdbc) {
      this.jdbc = requireNonNull(jdbc);
    }

    @Override
    public <T> Higher1<Id.µ, T> visit(DSL.Query<T> query) {
      Id<T> value =
          Id.of(jdbc.query(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Id.µ, Iterable<T>> visit(DSL.QueryIterable<T> query) {
      Id<Iterable<T>> value =
          Id.of(jdbc.queryIterable(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Id.µ, Option<T>> visit(DSL.QueryOne<T> query) {
      Id<Option<T>> value =
          Id.of(jdbc.queryOne(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public Higher1<Id.µ, Unit> visit(DSL.Update update) {
      Id<Unit> value = Id.of(jdbc.update(update.getQuery(), update.getParams()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Id.µ, Option<T>> visit(DSL.UpdateWithKeys<T> update) {
      Id<Option<T>> value =
          Id.of(jdbc.updateWithKeys(update.getQuery(), update.getParams(), update.getExtractor()));
      return value.kind1();
    }
  }

  private static class DSLTryVisitor implements DSL.Visitor<Try.µ> {

    private final JdbcTemplate jdbc;

    public DSLTryVisitor(JdbcTemplate jdbc) {
      this.jdbc = requireNonNull(jdbc);
    }

    @Override
    public <T> Higher1<Try.µ, T> visit(DSL.Query<T> query) {
      Try<T> value =
          Try.of(() -> jdbc.query(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Try.µ, Iterable<T>> visit(DSL.QueryIterable<T> query) {
      Try<Iterable<T>> value =
          Try.of(() -> jdbc.queryIterable(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Try.µ, Option<T>> visit(DSL.QueryOne<T> query) {
      Try<Option<T>> value =
          Try.of(() -> jdbc.queryOne(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public Higher1<Try.µ, Unit> visit(DSL.Update update) {
      Try<Unit> value = Try.of(() -> jdbc.update(update.getQuery(), update.getParams()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Try.µ, Option<T>> visit(DSL.UpdateWithKeys<T> update) {
      Try<Option<T>> value =
          Try.of(() -> jdbc.updateWithKeys(update.getQuery(), update.getParams(), update.getExtractor()));
      return value.kind1();
    }
  }

  private static class DSLUIOVisitor implements DSL.Visitor<UIO.µ> {

    private final JdbcTemplate jdbc;

    public DSLUIOVisitor(JdbcTemplate jdbc) {
      this.jdbc = requireNonNull(jdbc);
    }

    @Override
    public <T> Higher1<UIO.µ, T> visit(DSL.Query<T> query) {
      UIO<T> value =
          UIO.task(() -> jdbc.query(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<UIO.µ, Iterable<T>> visit(DSL.QueryIterable<T> query) {
      UIO<Iterable<T>> value =
          UIO.task(() -> jdbc.queryIterable(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<UIO.µ, Option<T>> visit(DSL.QueryOne<T> query) {
      UIO<Option<T>> value =
          UIO.task(() -> jdbc.queryOne(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public Higher1<UIO.µ, Unit> visit(DSL.Update update) {
      UIO<Unit> value = UIO.task(() -> jdbc.update(update.getQuery(), update.getParams()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<UIO.µ, Option<T>> visit(DSL.UpdateWithKeys<T> update) {
      UIO<Option<T>> value =
          UIO.task(() -> jdbc.updateWithKeys(update.getQuery(), update.getParams(), update.getExtractor()));
      return value.kind1();
    }
  }

  private static class DSLTaskVisitor implements DSL.Visitor<Task.µ> {

    private final JdbcTemplate jdbc;

    public DSLTaskVisitor(JdbcTemplate jdbc) {
      this.jdbc = requireNonNull(jdbc);
    }

    @Override
    public <T> Higher1<Task.µ, T> visit(DSL.Query<T> query) {
      Task<T> value =
          Task.task(() -> jdbc.query(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Task.µ, Iterable<T>> visit(DSL.QueryIterable<T> query) {
      Task<Iterable<T>> value =
          Task.task(() -> jdbc.queryIterable(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Task.µ, Option<T>> visit(DSL.QueryOne<T> query) {
      Task<Option<T>> value =
          Task.task(() -> jdbc.queryOne(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public Higher1<Task.µ, Unit> visit(DSL.Update update) {
      Task<Unit> value =
          Task.task(() -> jdbc.update(update.getQuery(), update.getParams()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Task.µ, Option<T>> visit(DSL.UpdateWithKeys<T> update) {
      Task<Option<T>> value =
          Task.task(() -> jdbc.updateWithKeys(update.getQuery(), update.getParams(), update.getExtractor()));
      return value.kind1();
    }
  }

  private static class DSLFutureVisitor implements DSL.Visitor<Future.µ> {

    private final JdbcTemplate jdbc;

    public DSLFutureVisitor(JdbcTemplate jdbc) {
      this.jdbc = requireNonNull(jdbc);
    }

    @Override
    public <T> Higher1<Future.µ, T> visit(DSL.Query<T> query) {
      Future<T> value =
          Future.async(() -> jdbc.query(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Future.µ, Iterable<T>> visit(DSL.QueryIterable<T> query) {
      Future<Iterable<T>> value =
          Future.async(() -> jdbc.queryIterable(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Future.µ, Option<T>> visit(DSL.QueryOne<T> query) {
      Future<Option<T>> value =
          Future.async(() -> jdbc.queryOne(query.getQuery(), query.getParams(), query.getExtractor()));
      return value.kind1();
    }

    @Override
    public Higher1<Future.µ, Unit> visit(DSL.Update update) {
      Future<Unit> value = Future.async(() -> jdbc.update(update.getQuery(), update.getParams()));
      return value.kind1();
    }

    @Override
    public <T> Higher1<Future.µ, Option<T>> visit(DSL.UpdateWithKeys<T> update) {
      Future<Option<T>> value = Future.async(() -> jdbc.updateWithKeys(update.getQuery(), update.getParams(), update.getExtractor()));
      return value.kind1();
    }
  }

  private static class DSLTransformer<F extends Kind> implements FunctionK<DSL.µ, F> {

    private final DSL.Visitor<F> visitor;

    public DSLTransformer(DSL.Visitor<F> visitor) {
      this.visitor = requireNonNull(visitor);
    }

    @Override
    public <T> Higher1<F, T> apply(Higher1<DSL.µ, T> from) {
      return DSL.narrowK(from).accept(visitor);
    }
  }
}

@Instance
interface PureDBCMonad extends Monad<PureDBC.µ> {

  @Override
  default <T> Higher1<PureDBC.µ, T> pure(T value) {
    return PureDBC.pure(value).kind1();
  }

  @Override
  default <T, R> Higher1<PureDBC.µ, R> map(Higher1<PureDBC.µ, T> value, Function1<T, R> mapper) {
    return value.fix1(PureDBC::narrowK).map(mapper).kind1();
  }

  @Override
  default <T, R> Higher1<PureDBC.µ, R> flatMap(
      Higher1<PureDBC.µ, T> value, Function1<T, ? extends Higher1<PureDBC.µ, R>> mapper) {
    return value.fix1(PureDBC::narrowK).flatMap(mapper.andThen(PureDBC::narrowK)).kind1();
  }
}

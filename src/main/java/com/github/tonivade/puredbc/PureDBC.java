/*
 * Copyright (c) 2020-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import static com.github.tonivade.purefun.core.Function1.cons;
import static com.github.tonivade.purefun.core.Precondition.checkNonNull;
import com.github.tonivade.puredbc.sql.Field;
import com.github.tonivade.puredbc.sql.SQL;
import com.github.tonivade.purefun.HigherKind;
import com.github.tonivade.purefun.Kind;
import com.github.tonivade.purefun.concurrent.Future;
import com.github.tonivade.purefun.concurrent.FutureOf;
import com.github.tonivade.purefun.core.Bindable;
import com.github.tonivade.purefun.core.Function1;
import com.github.tonivade.purefun.core.Unit;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.effect.Task;
import com.github.tonivade.purefun.effect.TaskOf;
import com.github.tonivade.purefun.effect.UIO;
import com.github.tonivade.purefun.effect.UIOOf;
import com.github.tonivade.purefun.free.Free;
import com.github.tonivade.purefun.type.Id;
import com.github.tonivade.purefun.type.IdOf;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.purefun.type.TryOf;
import com.github.tonivade.purefun.typeclasses.FunctionK;
import com.github.tonivade.purefun.typeclasses.Instances;
import com.github.tonivade.purefun.typeclasses.Monad;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@HigherKind
public final class PureDBC<T> implements PureDBCOf<T>, Bindable<PureDBC<?>, T> {

  private final Free<DSL<?>, T> value;

  private PureDBC(T value) {
    this(Free.pure(value));
  }

  private PureDBC(DSL<T> value) {
    this(Free.liftF(value));
  }

  private PureDBC(Free<DSL<?>, T> value) {
    this.value = checkNonNull(value);
  }

  @Override
  public <R> PureDBC<R> map(Function1<? super T, ? extends R> map) {
    return new PureDBC<>(value.map(map));
  }

  @Override
  public <R> PureDBC<R> flatMap(Function1<? super T, ? extends Kind<PureDBC<?>, ? extends R>> map) {
    return new PureDBC<>(value.flatMap(t -> map.andThen(PureDBCOf::toPureDBC).apply(t).value));
  }

  @Override
  public <R> PureDBC<R> andThen(Kind<PureDBC<?>, ? extends R> next) {
    return flatMap(cons(next));
  }

  public T unsafeRun(DataSource dataSource) {
    return unsafeRun(value).apply(dataSource);
  }

  public Try<T> safeRun(DataSource dataSource) {
    return safeRun(value).apply(dataSource);
  }

  public UIO<T> unsafeRunIO(DataSource dataSource) {
    return unsafeRunIO(value).apply(dataSource);
  }

  public Task<T> safeRunIO(DataSource dataSource) {
    return safeRunIO(value).apply(dataSource);
  }

  public Future<T> asyncRun(DataSource dataSource) {
    return asyncRun(value).apply(dataSource);
  }

  public Publisher<T> reactorRun(ConnectionFactory connectionFactory) {
    return reactorRun(value).apply(connectionFactory);
  }

  public static <T> PureDBC<T> pure(T value) {
    return new PureDBC<>(value);
  }

  public static PureDBC<Unit> update(SQL query) {
    return new PureDBC<>(new DSL.Update(query));
  }

  public static <T> PureDBC<Option<T>> updateWithKeys(SQL query, Field<T> field) {
    return new PureDBC<>(new DSL.UpdateWithKeys<>(query, field));
  }

  public static <T> PureDBC<Option<T>> queryMeta(SQL query, Function1<RowMetaData, T> rowMapper) {
    return new PureDBC<>(new DSL.QueryMeta<>(query, rowMapper));
  }

  public static <T> PureDBC<Option<T>> queryOne(SQL query, Function1<Row, T> rowMapper) {
    return new PureDBC<>(new DSL.QueryOne<>(query, rowMapper));
  }

  public static <T> PureDBC<Iterable<T>> queryIterable(SQL query, Function1<Row, T> rowMapper) {
    return new PureDBC<>(new DSL.QueryIterable<>(query, rowMapper));
  }

  public static Monad<PureDBC<?>> monad() {
    return PureDBCMonad.INSTANCE;
  }

  private static <A> Function1<DataSource, A> unsafeRun(Free<DSL<?>, A> free) {
    return dataSource -> {
      try (JdbcTemplate jdbc = newTemplate(dataSource)) {
        DSLIdVisitor visitor = new DSLIdVisitor(jdbc);
        Kind<Id<?>, A> foldMap = free.foldMap(Instances.monad(), new DSLTransformer<>(visitor));
        return foldMap.fix(IdOf::toId).value();
      }
    };
  }

  private static <A> Function1<DataSource, Try<A>> safeRun(Free<DSL<?>, A> free) {
    return dataSource -> {
      try (JdbcTemplate jdbc = newTemplate(dataSource)) {
        DSLTryVisitor visitor = new DSLTryVisitor(jdbc);
        Kind<Try<?>, A> foldMap = free.foldMap(Instances.monad(), new DSLTransformer<>(visitor));
        return foldMap.fix(TryOf::toTry);
      }
    };
  }

  private static <A> Function1<DataSource, UIO<A>> unsafeRunIO(Free<DSL<?>, A> free) {
    return dataSource ->
      UIO.bracket(UIO.task(() -> newTemplate(dataSource)), jdbc -> {
        DSLUIOVisitor visitor = new DSLUIOVisitor(jdbc);
        Kind<UIO<?>, A> foldMap = free.foldMap(Instances.monad(), new DSLTransformer<>(visitor));
        return foldMap.fix(UIOOf::toUIO);
      });
  }

  private static <A> Function1<DataSource, Task<A>> safeRunIO(Free<DSL<?>, A> free) {
    return dataSource ->
      Task.bracket(Task.task(() -> newTemplate(dataSource)), jdbc -> {
        DSLTaskVisitor visitor = new DSLTaskVisitor(jdbc);
        Kind<Task<?>, A> foldMap = free.foldMap(Instances.monad(), new DSLTransformer<>(visitor));
        return foldMap.fix(TaskOf::toTask);
      });
  }

  private static <A> Function1<DataSource, Future<A>> asyncRun(Free<DSL<?>, A> free) {
    return dataSource ->
        Future.bracket(Future.task(() -> newTemplate(dataSource)), jdbc -> {
          DSLFutureVisitor visitor = new DSLFutureVisitor(jdbc);
          Kind<Future<?>, A> foldMap = free.foldMap(Instances.monad(), new DSLTransformer<>(visitor));
          return foldMap.fix(FutureOf::toFuture);
        });
  }

  private static <A> Function1<ConnectionFactory, Publisher<A>> reactorRun(Free<DSL<?>, A> free) {
    return connectionFactory -> {
      R2dbcTemplate r2dbc = newTemplate(connectionFactory);
      DSLReactVisitor visitor = new DSLReactVisitor(r2dbc);
      return free.foldMap(PublisherKMonad.INSTANCE, new DSLTransformer<>(visitor)).fix(PublisherKOf::toPublisherK);
    };
  }

  private static JdbcTemplate newTemplate(DataSource dataSource) throws SQLException {
    return new JdbcTemplate(dataSource.getConnection());
  }

  private static R2dbcTemplate newTemplate(ConnectionFactory connectionFactory) {
    return new R2dbcTemplate(connectionFactory);
  }

  private static class DSLIdVisitor implements DSL.Visitor<Id<?>> {

    private final JdbcTemplate jdbc;

    public DSLIdVisitor(JdbcTemplate jdbc) {
      this.jdbc = checkNonNull(jdbc);
    }

    @Override
    public <T> Id<Option<T>> visit(DSL.QueryMeta<T> query) {
      return Id.of(jdbc.queryMeta(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public <T> Id<Iterable<T>> visit(DSL.QueryIterable<T> query) {
      return Id.of(jdbc.queryIterable(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public <T> Id<Option<T>> visit(DSL.QueryOne<T> query) {
      return Id.of(jdbc.queryOne(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public Id<Unit> visit(DSL.Update update) {
      return Id.of(jdbc.update(update.query().getQuery(), update.query().getParams()));
    }

    @Override
    public <T> Id<Option<T>> visit(DSL.UpdateWithKeys<T> update) {
      return Id.of(jdbc.updateWithKeys(update.query().getQuery(), update.query().getParams(), update.field()));
    }
  }

  private static class DSLTryVisitor implements DSL.Visitor<Try<?>> {

    private final JdbcTemplate jdbc;

    public DSLTryVisitor(JdbcTemplate jdbc) {
      this.jdbc = checkNonNull(jdbc);
    }

    @Override
    public <T> Try<Option<T>> visit(DSL.QueryMeta<T> query) {
      return Try.of(() -> jdbc.queryMeta(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public <T> Try<Iterable<T>> visit(DSL.QueryIterable<T> query) {
      return Try.of(() -> jdbc.queryIterable(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public <T> Try<Option<T>> visit(DSL.QueryOne<T> query) {
      return Try.of(() -> jdbc.queryOne(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public Try<Unit> visit(DSL.Update update) {
      return Try.of(() -> jdbc.update(update.query().getQuery(), update.query().getParams()));
    }

    @Override
    public <T> Try<Option<T>> visit(DSL.UpdateWithKeys<T> update) {
      return Try.of(() -> jdbc.updateWithKeys(update.query().getQuery(), update.query().getParams(), update.field()));
    }
  }

  private static class DSLUIOVisitor implements DSL.Visitor<UIO<?>> {

    private final JdbcTemplate jdbc;

    public DSLUIOVisitor(JdbcTemplate jdbc) {
      this.jdbc = checkNonNull(jdbc);
    }

    @Override
    public <T> UIO<Option<T>> visit(DSL.QueryMeta<T> query) {
      return UIO.task(() -> jdbc.queryMeta(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public <T> UIO<Iterable<T>> visit(DSL.QueryIterable<T> query) {
      return UIO.task(() -> jdbc.queryIterable(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public <T> UIO<Option<T>> visit(DSL.QueryOne<T> query) {
      return UIO.task(() -> jdbc.queryOne(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public UIO<Unit> visit(DSL.Update update) {
      return UIO.task(() -> jdbc.update(update.query().getQuery(), update.query().getParams()));
    }

    @Override
    public <T> UIO<Option<T>> visit(DSL.UpdateWithKeys<T> update) {
      return UIO.task(() -> jdbc.updateWithKeys(update.query().getQuery(), update.query().getParams(), update.field()));
    }
  }

  private static class DSLTaskVisitor implements DSL.Visitor<Task<?>> {

    private final JdbcTemplate jdbc;

    public DSLTaskVisitor(JdbcTemplate jdbc) {
      this.jdbc = checkNonNull(jdbc);
    }

    @Override
    public <T> Task<Option<T>> visit(DSL.QueryMeta<T> query) {
      return Task.task(() -> jdbc.queryMeta(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public <T> Task<Iterable<T>> visit(DSL.QueryIterable<T> query) {
      return Task.task(() -> jdbc.queryIterable(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public <T> Task<Option<T>> visit(DSL.QueryOne<T> query) {
      return Task.task(() -> jdbc.queryOne(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public Task<Unit> visit(DSL.Update update) {
      return Task.task(() -> jdbc.update(update.query().getQuery(), update.query().getParams()));
    }

    @Override
    public <T> Task<Option<T>> visit(DSL.UpdateWithKeys<T> update) {
      return Task.task(() -> jdbc.updateWithKeys(update.query().getQuery(), update.query().getParams(), update.field()));
    }
  }

  private static class DSLFutureVisitor implements DSL.Visitor<Future<?>> {

    private final JdbcTemplate jdbc;

    public DSLFutureVisitor(JdbcTemplate jdbc) {
      this.jdbc = checkNonNull(jdbc);
    }

    @Override
    public <T> Future<Option<T>> visit(DSL.QueryMeta<T> query) {
      return Future.task(() -> jdbc.queryMeta(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public <T> Future<Iterable<T>> visit(DSL.QueryIterable<T> query) {
      return Future.task(() -> jdbc.queryIterable(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public <T> Future<Option<T>> visit(DSL.QueryOne<T> query) {
      return Future.task(() -> jdbc.queryOne(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public Future<Unit> visit(DSL.Update update) {
      return Future.task(() -> jdbc.update(update.query().getQuery(), update.query().getParams()));
    }

    @Override
    public <T> Future<Option<T>> visit(DSL.UpdateWithKeys<T> update) {
      return Future.task(() -> jdbc.updateWithKeys(update.query().getQuery(), update.query().getParams(), update.field()));
    }
  }

  private static class DSLReactVisitor implements DSL.Visitor<PublisherK<?>> {

    private final R2dbcTemplate r2dbc;

    private DSLReactVisitor(R2dbcTemplate r2dbc) {
      this.r2dbc = checkNonNull(r2dbc);
    }

    @Override
    public <T> PublisherK<Option<T>> visit(DSL.QueryMeta<T> query) {
      return PublisherK.from(r2dbc.queryMeta(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }

    @Override
    public PublisherK<Unit> visit(DSL.Update update) {
      return PublisherK.from(r2dbc.update(update.query().getQuery(), update.query().getParams()));
    }

    @Override
    public <T> PublisherK<Option<T>> visit(DSL.UpdateWithKeys<T> update) {
      return PublisherK.from(r2dbc.updateWithKeys(update.query().getQuery(), update.query().getParams(), update.field()));
    }

    @Override
    public <T> PublisherK<Iterable<T>> visit(DSL.QueryIterable<T> query) {
      return PublisherK.from(r2dbc.queryIterable(query.query().getQuery(), query.query().getParams(), query.rowMapper()))
          .map(ImmutableList::from);
    }

    @Override
    public <T> PublisherK<Option<T>> visit(DSL.QueryOne<T> query) {
      return PublisherK.from(r2dbc.queryOne(query.query().getQuery(), query.query().getParams(), query.rowMapper()));
    }
  }

  private static class DSLTransformer<F extends Kind<F, ?>> implements FunctionK<DSL<?>, F> {

    private final DSL.Visitor<F> visitor;

    public DSLTransformer(DSL.Visitor<F> visitor) {
      this.visitor = checkNonNull(visitor);
    }

    @Override
    public <T> Kind<F, T> apply(Kind<DSL<?>, ? extends T> from) {
      return from.fix(DSLOf::<T>toDSL).accept(visitor);
    }
  }
}

interface PureDBCMonad extends Monad<PureDBC<?>> {

  PureDBCMonad INSTANCE = new PureDBCMonad() { };

  @Override
  default <T> PureDBC<T> pure(T value) {
    return PureDBC.pure(value);
  }

  @Override
  default <T, R> PureDBC<R> flatMap(
      Kind<PureDBC<?>, ? extends T> value, Function1<? super T, ? extends Kind<PureDBC<?>, ? extends R>> mapper) {
    return value.fix(PureDBCOf::toPureDBC).flatMap(mapper.andThen(PureDBCOf::toPureDBC));
  }
}

interface PublisherKMonad extends Monad<PublisherK<?>> {

  PublisherKMonad INSTANCE = new PublisherKMonad() {};

  @Override
  default <T, R> PublisherK<R> map(Kind<PublisherK<?>, ? extends T> value, Function1<? super T, ? extends R> map) {
    return value.fix(PublisherKOf::toPublisherK).map(map);
  }

  @Override
  default <T, R> PublisherK<R> flatMap(
      Kind<PublisherK<?>, ? extends T> value, Function1<? super T, ? extends Kind<PublisherK<?>, ? extends R>> map) {
    return value.fix(PublisherKOf::toPublisherK).flatMap(map.andThen(PublisherKOf::toPublisherK));
  }

  @Override
  default <T> PublisherK<T> pure(T value) {
    return PublisherK.pure(value);
  }
}

@HigherKind
final class PublisherK<T> implements PublisherKOf<T>, Publisher<T> {

  private final Publisher<? extends T> value;

  private PublisherK(Publisher<? extends T> value) {
    this.value = checkNonNull(value);
  }

  public <R> PublisherK<R> map(Function1<? super T, ? extends R> mapper) {
    if (value instanceof Mono) {
      return new PublisherK<>(Mono.from(value).map(mapper::apply));
    }
    return new PublisherK<>(Flux.from(value).map(mapper::apply));
  }

  public <R> PublisherK<R> flatMap(Function1<? super T, PublisherK<? extends R>> mapper) {
    if (value instanceof Mono) {
      return new PublisherK<>(Mono.from(value).flatMap(mapper.andThen(Mono::from)::apply));
    }
    return new PublisherK<>(Flux.from(value).flatMap(mapper.andThen(Flux::from)::apply));
  }

  public static <T> PublisherK<T> from(Publisher<? extends T> value) {
    return new PublisherK<>(value);
  }

  public static <T> PublisherK<T> pure(T value) {
    return from(Mono.just(value));
  }

  @Override
  public void subscribe(Subscriber<? super T> subscriber) {
    value.subscribe(subscriber);
  }
}

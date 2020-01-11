/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc;

import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Higher1;
import com.github.tonivade.purefun.Unit;
import com.github.tonivade.purefun.concurrent.Future;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.effect.UIO;
import com.github.tonivade.purefun.free.Free;
import com.github.tonivade.purefun.instances.FutureInstances;
import com.github.tonivade.purefun.instances.IdInstances;
import com.github.tonivade.purefun.instances.TryInstances;
import com.github.tonivade.purefun.instances.UIOInstances;
import com.github.tonivade.purefun.type.Id;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.purefun.typeclasses.Transformer;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static com.github.tonivade.purefun.Function1.cons;
import static com.github.tonivade.purefun.free.Free.liftF;
import static java.util.Objects.requireNonNull;

public final class PureDBC<T>  {

  private final Free<DSL.µ, T> value;

  protected PureDBC(DSL<T> value) {
    this(liftF(value.kind1()));
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

  public UIO<T> runIO(DataSource dataSource) {
    return runIO(value).compose(JdbcTemplate::new).apply(dataSource);
  }

  public Future<T> asyncRun(DataSource dataSource) {
    return asyncRun(value).compose(JdbcTemplate::new).apply(dataSource);
  }

  public static PureDBC<Unit> update(Bindable query) {
    return new PureDBC<>(new DSL.Update(query));
  }

  public static <T> PureDBC<Option<T>> queryOne(Bindable query, Function1<ResultSet, T> rowMapper) {
    return new PureDBC<>(new DSL.Query<>(query, option(rowMapper)));
  }

  public static <T> PureDBC<Iterable<T>> query(Bindable query, Function1<ResultSet, T> rowMapper) {
    return new PureDBC<>(new DSL.Query<>(query, iterable(rowMapper)));
  }

  private static <T> Function1<ResultSet, Option<T>> option(Function1<ResultSet, T> rowMapper) {
    return resultSet -> {
      if (resultSet.next()) {
        return Option.some(rowMapper.apply(resultSet));
      }
      return Option.none();
    };
  }

  private static <T> Function1<ResultSet, Iterable<T>> iterable(Function1<ResultSet, T> rowMapper) {
    return resultSet -> {
      List<T> result = new ArrayList<>();
      while (resultSet.next()) {
        result.add(rowMapper.apply(resultSet));
      }
      return ImmutableList.from(result);
    };
  }

  private static <A> Function1<JdbcTemplate, A> unsafeRun(Free<DSL.µ, A> free) {
    return jdbc -> {
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

  private static <A> Function1<JdbcTemplate, Try<A>> safeRun(Free<DSL.µ, A> free) {
    return jdbc -> {
      Higher1<Try.µ, A> foldMap = free.foldMap(TryInstances.monad(), new Transformer<DSL.µ, Try.µ>() {
        @Override
        public <T> Higher1<Try.µ, T> apply(Higher1<DSL.µ, T> from) {
          DSL<T> dsl = DSL.narrowK(from);
          if (dsl instanceof DSL.Update) {
            DSL.Update update = (DSL.Update) dsl;
            return (Higher1<Try.µ, T>) Try.of(() -> jdbc.update(update.getQuery(), update.getParams())).kind1();
          }
          if (dsl instanceof DSL.Query) {
            DSL.Query<T> query = (DSL.Query<T>) dsl;
            return (Higher1<Try.µ, T>) Try.of(() -> jdbc.query(query.getQuery(), query.getParams(), query.getExtractor())).kind1();
          }
          throw new IllegalStateException();
        }
      });
      return foldMap.fix1(Try::narrowK);
    };
  }

  private static <A> Function1<JdbcTemplate, UIO<A>> runIO(Free<DSL.µ, A> free) {
    return jdbc -> {
      Higher1<UIO.µ, A> foldMap = free.foldMap(UIOInstances.monad(), new Transformer<DSL.µ, UIO.µ>() {
        @Override
        public <T> Higher1<UIO.µ, T> apply(Higher1<DSL.µ, T> from) {
          DSL<T> dsl = DSL.narrowK(from);
          if (dsl instanceof DSL.Update) {
            DSL.Update update = (DSL.Update) dsl;
            return (Higher1<UIO.µ, T>) UIO.task(() -> jdbc.update(update.getQuery(), update.getParams())).kind1();
          }
          if (dsl instanceof DSL.Query) {
            DSL.Query<T> query = (DSL.Query<T>) dsl;
            return (Higher1<UIO.µ, T>) UIO.task(() -> jdbc.query(query.getQuery(), query.getParams(), query.getExtractor())).kind1();
          }
          throw new IllegalStateException();
        }
      });
      return foldMap.fix1(UIO::narrowK);
    };
  }

  private static <A> Function1<JdbcTemplate, Future<A>> asyncRun(Free<DSL.µ, A> free) {
    return jdbc -> {
      Higher1<Future.µ, A> foldMap = free.foldMap(FutureInstances.monad(), new Transformer<DSL.µ, Future.µ>() {
        @Override
        public <T> Higher1<Future.µ, T> apply(Higher1<DSL.µ, T> from) {
          DSL<T> dsl = DSL.narrowK(from);
          if (dsl instanceof DSL.Update) {
            DSL.Update update = (DSL.Update) dsl;
            return (Higher1<Future.µ, T>) Future.async(() -> jdbc.update(update.getQuery(), update.getParams())).kind1();
          }
          if (dsl instanceof DSL.Query) {
            DSL.Query<T> query = (DSL.Query<T>) dsl;
            return (Higher1<Future.µ, T>) Future.async(() -> jdbc.query(query.getQuery(), query.getParams(), query.getExtractor())).kind1();
          }
          throw new IllegalStateException();
        }
      });
      return foldMap.fix1(Future::narrowK);
    };
  }
}

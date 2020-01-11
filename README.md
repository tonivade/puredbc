# PureDBC

Pure Functional Database Connection Layer

## Disclaimer

This project is experimental, so don't try to use in production.

## Objective

Develop a comprehensible and easy to use API:

- High level
- Type safety
- Composable
- Meaningful errors

## Example

```java
  // Query DSL
  Bindable createTable = sql("create table if not exists test (id int primary key, name varchar(100))");
  Bindable dropTable = sql("drop table if exists test");
  Bindable deleteAll = deleteFrom("test");
  Bindable1<Integer> deleteOne = deleteFrom("test").where("id = ?");
  Bindable2<Integer, String> insertRow = insertInto("test").values("id", "name");
  Bindable2<String, Integer> updateRow = update("test").<String>set("name").where("id = ?");
  Bindable findAll = select("id", "name").from("test");
  Bindable1<Integer> findOne = select("id", "name").from("test").where("id = ?");
  
  // PureDBC DSL
  PureDBC<Iterable<Tuple2<Integer, String>>> program =
    update(createTable)
      .andThen(update(deleteAll))
      .andThen(update(insertRow.bind(1, "toni")))
      .andThen(update(insertRow.bind(2, "pepe")))
      .andThen(query(findAll, this::asTuple));
  
  assertEquals(
      listOf(Tuple.of(1, "toni"), Tuple.of(2, "pepe")), 
      program.unsafeRun(dataSource()))
```

## License

Distributed under MIT License

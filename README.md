# PureDBC

Pure Functional Database Connection Layer

## Disclaimer

This project is experimental, so don't try to use in production.

## Objective

Develop a comprehensible and easy to use API:

- High level
- Flexible
- Type safe
- Composable
- Meaningful errors

## Example

```java
  // Query DSL
  SQL createTable = sql("create table if not exists test (id int primary key, name varchar(100))");
  SQL dropTable = sql("drop table if exists test");
  SQL deleteAll = delete("test");
  SQL1<Integer> deleteOne = delete("test").where("id = ?");
  SQL2<Integer, String> insertRow = insert("test").values("id", "name");
  SQL2<String, Integer> updateRow = update("test").<String>set("name").where("id = ?");
  SQL findAll = select("id", "name").from("test");
  SQL1<Integer> findOne = select("id", "name").from("test").where("id = ?");
  
  // PureDBC DSL
  PureDBC<Iterable<Tuple2<Integer, String>>> program =
    update(createTable)
      .andThen(update(deleteAll))
      .andThen(update(insertRow.bind(1, "toni")))
      .andThen(update(insertRow.bind(2, "pepe")))
      .andThen(queryIterable(findAll, this::asTuple));
  
  assertEquals(
      listOf(Tuple.of(1, "toni"), Tu:ple.of(2, "pepe")), 
      program.unsafeRun(dataSource));
```

## License

Distributed under MIT License

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
  // Table instance
  Test TEST = new Test();
  
  // Query DSL
  SQL createTable = sql(
      "create table if not exists test(",
      "id identity primary key,",
      "name varchar(100))");
  SQL dropTable = sql("drop table if exists test");
  SQL deleteAll = deleteFrom(TEST);
  SQL1<Integer> deleteOne = deleteFrom(TEST).where(TEST.ID.eq());
  SQL1<String> insertRowWithKey = insertInto(TEST).values(TEST.NAME);
  SQL2<Integer, String> insertRow = insertInto(TEST).values(TEST.ID, TEST.NAME);
  SQL2<String, Integer> updateRow = update(TEST).set(TEST.NAME).where(TEST.ID.eq());
  SQL findAll = select(TEST.ID, TEST.NAME).from(TEST);
  SQL count = select(TEST.ID.count().as("elements")).from(TEST);
  SQL1<Integer> findOne = select(TEST.ID, TEST.NAME).from(TEST).where(TEST.ID.eq());
  
  // PureDBC DSL
  PureDBC<Iterable<Tuple2<Integer, String>>> program =
    update(createTable)
      .andThen(update(deleteAll))
      .andThen(update(insertRow.bind(1, "toni")))
      .andThen(update(insertRow.bind(2, "pepe")))
      .andThen(queryIterable(findAll, TEST::asTuple));
  
  assertEquals(
      listOf(Tuple.of(1, "toni"), Tuple.of(2, "pepe")), 
      program.unsafeRun(dataSource));
```

## License

Distributed under MIT License

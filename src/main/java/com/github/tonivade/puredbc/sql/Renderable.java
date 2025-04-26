/*
 * Copyright (c) 2020-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.puredbc.sql;

public sealed interface Renderable permits Field {
  
  default String render() {
    if (this instanceof Function<?> function) {
      return function.name() + function.params().join(", ", "(", ")");
    }
    if (this instanceof Alias<?> alias) {
      return alias.field().render() + " as " + alias.name();
    }
    if (this instanceof TableField<?> tableField) {
      return tableField.table() + "." + tableField.field().render();
    }
    if (this instanceof Field<?> field) {
      return field.name();
    }
    throw new IllegalStateException();
  }
}

module com.github.tonivade.puredbc {
  exports com.github.tonivade.puredbc.sql;
  exports com.github.tonivade.puredbc;

  requires transitive com.github.tonivade.purefun;
  requires transitive com.github.tonivade.purefun.core;
  requires transitive com.github.tonivade.purefun.effect;
  requires transitive com.github.tonivade.purefun.free;
  requires transitive com.github.tonivade.purefun.typeclasses;
  requires transitive java.sql;
  requires org.reactivestreams;
  requires r2dbc.spi;
  requires reactor.core;
  requires transitive java.compiler;
}
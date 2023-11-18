module com.github.tonivade.puredbc {
  exports com.github.tonivade.puredbc.sql;
  exports com.github.tonivade.puredbc;

  requires com.github.tonivade.purefun.annotation;
  requires com.github.tonivade.purefun.core;
  requires com.github.tonivade.purefun.effect;
  requires com.github.tonivade.purefun.free;
  requires com.github.tonivade.purefun.typeclasses;
  requires java.sql;
  requires org.reactivestreams;
  requires r2dbc.spi;
  requires reactor.core;
  requires java.compiler;
}
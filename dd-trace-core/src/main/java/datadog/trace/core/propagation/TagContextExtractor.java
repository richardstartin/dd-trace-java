package datadog.trace.core.propagation;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;

import java.util.Map;

public class TagContextExtractor implements HttpCodec.Extractor {

  private final ThreadLocal<ContextInterpreter> ctxInterpreter;

  public TagContextExtractor(final Map<String, String> taggedHeaders, final ContextInterpreter.Factory factory) {
    this.ctxInterpreter = new ThreadLocal<ContextInterpreter>() {
      @Override
      protected ContextInterpreter initialValue() {
        return factory.create(taggedHeaders);
      }
    };
  }

  @Override
  public <C> TagContext extract(final C carrier, final AgentPropagation.ContextVisitor<C> getter) {
    ContextInterpreter interpreter = this.ctxInterpreter.get().reset();
    getter.forEachKey(carrier, interpreter, interpreter);
    return interpreter.build();
  }
}

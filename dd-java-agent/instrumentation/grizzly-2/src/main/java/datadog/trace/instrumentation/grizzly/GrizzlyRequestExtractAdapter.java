package datadog.trace.instrumentation.grizzly;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;
import org.glassfish.grizzly.http.server.Request;

public class GrizzlyRequestExtractAdapter implements AgentPropagation.ContextVisitor<Request> {

  public static final GrizzlyRequestExtractAdapter GETTER = new GrizzlyRequestExtractAdapter();

  @Override
  public void forEachKey(
      Request carrier,
      AgentPropagation.KeyClassifier classifier,
      AgentPropagation.KeyValueConsumer consumer) {
    for (String header : carrier.getHeaderNames()) {
      String lowerCaseKey = header.toLowerCase();
      int classification = classifier.classify(lowerCaseKey);
      if (classification != -1) {
        if (!consumer.accept(classification, lowerCaseKey, carrier.getHeader(header))) {
          return;
        }
      }
    }
  }
}

package datadog.trace.instrumentation.akkahttp;

import akka.http.javadsl.model.HttpHeader;
import akka.http.scaladsl.model.HttpRequest;
import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;

public class AkkaHttpServerHeaders implements AgentPropagation.ContextVisitor<HttpRequest> {

  public static final AkkaHttpServerHeaders GETTER = new AkkaHttpServerHeaders();

  @Override
  public void forEachKey(
      final HttpRequest carrier,
      final AgentPropagation.KeyClassifier classifier,
      final AgentPropagation.KeyValueConsumer consumer) {
    for (final HttpHeader header : carrier.getHeaders()) {
      String name = header.lowercaseName();
      int classification = classifier.classify(name);
      if (classification != -1) {
        if (!consumer.accept(classification, name, header.value())) {
          return;
        }
      }
    }
  }
}

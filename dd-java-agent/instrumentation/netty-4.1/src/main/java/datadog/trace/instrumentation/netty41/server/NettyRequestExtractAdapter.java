package datadog.trace.instrumentation.netty41.server;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;
import io.netty.handler.codec.http.HttpHeaders;
import java.util.Map;

public class NettyRequestExtractAdapter implements AgentPropagation.ContextVisitor<HttpHeaders> {

  public static final NettyRequestExtractAdapter GETTER = new NettyRequestExtractAdapter();

  @Override
  public void forEachKey(
      HttpHeaders carrier,
      AgentPropagation.KeyClassifier classifier,
      AgentPropagation.KeyValueConsumer consumer) {
    for (Map.Entry<String, String> header : carrier) {
      String lowerCaseKey = header.getKey().toLowerCase();
      int classification = classifier.classify(lowerCaseKey);
      if (classification != -1) {
        if (!consumer.accept(classification, lowerCaseKey, header.getValue())) {
          return;
        }
      }
    }
  }
}

package datadog.trace.instrumentation.kafka_streams;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

public class TextMapExtractAdapter implements AgentPropagation.ContextVisitor<Headers> {

  public static final TextMapExtractAdapter GETTER = new TextMapExtractAdapter();

  @Override
  public void forEachKey(
      Headers carrier,
      AgentPropagation.KeyClassifier classifier,
      AgentPropagation.KeyValueConsumer consumer) {
    for (Header header : carrier) {
      String lowerCaseKey = header.key();
      int classification = classifier.classify(lowerCaseKey);
      if (classification != -1) {
        byte[] value = header.value();
        if (null != value) {
          if (!consumer.accept(classification, lowerCaseKey, new String(header.value()))) {
            return;
          }
        }
      }
    }
  }
}

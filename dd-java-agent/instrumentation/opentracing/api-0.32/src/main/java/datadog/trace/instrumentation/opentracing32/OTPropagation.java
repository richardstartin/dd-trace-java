package datadog.trace.instrumentation.opentracing32;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;
import io.opentracing.propagation.TextMapExtract;
import io.opentracing.propagation.TextMapInject;
import java.util.HashMap;
import java.util.Map;

class OTPropagation {

  static class TextMapInjectSetter implements AgentPropagation.Setter<TextMapInject> {
    static final TextMapInjectSetter INSTANCE = new TextMapInjectSetter();

    @Override
    public void set(final TextMapInject carrier, final String key, final String value) {
      carrier.put(key, value);
    }
  }

  static class TextMapExtractGetter implements AgentPropagation.ContextVisitor<TextMapExtract> {
    private final Map<String, String> extracted = new HashMap<>();

    TextMapExtractGetter(final TextMapExtract carrier) {
      for (final Map.Entry<String, String> entry : carrier) {
        extracted.put(entry.getKey(), entry.getValue());
      }
    }

    @Override
    public void forEachKey(
        TextMapExtract carrier,
        AgentPropagation.KeyClassifier classifier,
        AgentPropagation.KeyValueConsumer consumer) {
      // So using "extracted" is valid
      for (Map.Entry<String, String> entry : extracted.entrySet()) {
        String lowerCaseKey = entry.getKey().toLowerCase();
        int classification = classifier.classify(lowerCaseKey);
        if (classification != -1) {
          if (!consumer.accept(classification, lowerCaseKey, entry.getValue())) {
            return;
          }
        }
      }
    }
  }
}

package datadog.trace.instrumentation.grpc.server;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;
import io.grpc.Metadata;
import java.util.ArrayList;
import java.util.List;

public final class GrpcExtractAdapter implements AgentPropagation.ContextVisitor<Metadata> {

  public static final GrpcExtractAdapter GETTER = new GrpcExtractAdapter();

  @Override
  public void forEachKey(Metadata carrier, AgentPropagation.KeyClassifier classifier, AgentPropagation.KeyValueConsumer consumer) {
    for (String key : carrier.keys()) {
      if (!key.endsWith(Metadata.BINARY_HEADER_SUFFIX)) {
        String lowerCaseKey = key.toLowerCase();
        int classification = classifier.classify(lowerCaseKey);
        if (classification != -1) {
          consumer.accept(classification, lowerCaseKey,
            carrier.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER)));
        }
      }
    }
  }
}

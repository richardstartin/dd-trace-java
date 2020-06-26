package datadog.trace.instrumentation.jetty8;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class HttpServletRequestExtractAdapter
    implements AgentPropagation.ContextVisitor<HttpServletRequest> {

  public static final HttpServletRequestExtractAdapter GETTER =
      new HttpServletRequestExtractAdapter();

  @Override
  public void forEachKey(HttpServletRequest carrier, AgentPropagation.KeyClassifier classifier, AgentPropagation.KeyValueConsumer consumer) {
    Enumeration<String> headerNames = carrier.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String header = headerNames.nextElement();
      String lowerCaseKey = header.toLowerCase();
      int classification = classifier.classify(lowerCaseKey);
      if (classification != -1) {
        consumer.accept(classification, lowerCaseKey, carrier.getHeader(header));
      }
    }
  }
}

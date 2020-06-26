package datadog.trace.agent.test.server.http;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Tracer extract adapter for {@link HttpServletRequest}.
 *
 * @author Pavol Loffay
 */
// FIXME:  This code is duplicated in several places.  Extract to a common dependency.
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

package datadog.trace.core.propagation;

import datadog.trace.api.DDId;
import datadog.trace.api.sampling.PrioritySampling;
import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;

import java.util.Map;

public abstract class ContextInterpreter implements AgentPropagation.KeyValueConsumer, AgentPropagation.KeyClassifier {

  protected final Map<String, String> taggedHeaders;

  protected DDId traceId = DDId.ZERO;
  protected DDId spanId = DDId.ZERO;
  protected int samplingPriority = PrioritySampling.UNSET;
  protected Map<String, String> tags = null;
  protected Map<String, String> baggage = null;
  protected String origin = null;

  protected ContextInterpreter(Map<String, String> taggedHeaders) {
    this.taggedHeaders = taggedHeaders;
  }

  public interface Factory {
    ContextInterpreter create(Map<String, String> tagsMapping);
  }

  public ContextInterpreter reset() {
    traceId = DDId.ZERO;
    spanId = DDId.ZERO;
    samplingPriority = PrioritySampling.UNSET;
    origin = null;
    tags = null;
    baggage = null;
    return this;
  }

  TagContext build() {
    if (!DDId.ZERO.equals(traceId)) {
      final ExtractedContext context =
        new ExtractedContext(traceId, spanId, samplingPriority, origin, baggage, tags);
      context.lockSamplingPriority();
      return context;
    } else if (origin != null || tags != null) {
      return new TagContext(origin, tags);
    }
    return null;
  }
}

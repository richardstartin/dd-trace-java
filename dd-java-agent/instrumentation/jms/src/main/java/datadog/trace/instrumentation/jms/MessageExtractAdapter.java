package datadog.trace.instrumentation.jms;

import static datadog.trace.bootstrap.instrumentation.api.AgentPropagation.KeyClassifier.IGNORE;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;
import java.util.Enumeration;
import javax.jms.JMSException;
import javax.jms.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageExtractAdapter implements AgentPropagation.ContextVisitor<Message> {

  public static final MessageExtractAdapter GETTER = new MessageExtractAdapter();

  @Override
  public void forEachKey(
      Message carrier,
      AgentPropagation.KeyClassifier classifier,
      AgentPropagation.KeyValueConsumer consumer) {
    try {
      final Enumeration<?> enumeration = carrier.getPropertyNames();
      if (null != enumeration) {
        while (enumeration.hasMoreElements()) {
          String key = (String) enumeration.nextElement();
          String lowerCaseKey = key.toLowerCase();
          int classification = classifier.classify(lowerCaseKey);
          if (classification != IGNORE) {
            Object value = carrier.getObjectProperty(key);
            if (!consumer.accept(classification, lowerCaseKey, String.valueOf(value))) {
              return;
            }
          }
        }
      }
    } catch (JMSException e) {
      throw new RuntimeException(e);
    }
  }
}

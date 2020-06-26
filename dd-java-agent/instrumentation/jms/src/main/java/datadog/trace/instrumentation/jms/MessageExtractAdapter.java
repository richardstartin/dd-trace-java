package datadog.trace.instrumentation.jms;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageExtractAdapter implements AgentPropagation.ContextVisitor<Message> {

  public static final MessageExtractAdapter GETTER = new MessageExtractAdapter();

  @Override
  public void forEachKey(Message carrier,
                         AgentPropagation.KeyClassifier classifier,
                         AgentPropagation.KeyValueConsumer consumer) {
    try {
      final Enumeration<?> enumeration = carrier.getPropertyNames();
      if (null != enumeration) {
        while (enumeration.hasMoreElements()) {
          String key = (String) enumeration.nextElement();
          String lowerCaseKey = key.toLowerCase();
          int classification = classifier.classify(lowerCaseKey);
          if (classification != -1) {
            Object value = carrier.getObjectProperty(key);
            consumer.accept(classification, lowerCaseKey, String.valueOf(value));
          }
        }
      }
    } catch (JMSException e) {
      throw new RuntimeException(e);
    }
  }
}

package datadog.trace.instrumentation.play26;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;
import java.util.ArrayList;
import java.util.List;
import play.api.mvc.Headers;
import scala.Option;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.Seq;

public class PlayHeaders implements AgentPropagation.ContextVisitor<Headers> {

  public static final PlayHeaders GETTER = new PlayHeaders();

  @Override
  public void forEachKey(Headers carrier, AgentPropagation.KeyClassifier classifier, AgentPropagation.KeyValueConsumer consumer) {
    for (String entry : JavaConversions.asJavaIterable(carrier.keys())) {
      String lowerCaseKey = entry.toLowerCase();
      int classification = classifier.classify(lowerCaseKey);
      if (classification != -1) {
        Option<String> value = carrier.get(entry);
        if (value.nonEmpty()) {
          consumer.accept(classification, lowerCaseKey, value.get());
        }
      }
    }
  }
}

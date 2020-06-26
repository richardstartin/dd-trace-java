package datadog.trace.instrumentation.play23;

import datadog.trace.bootstrap.instrumentation.api.AgentPropagation;
import play.api.mvc.Headers;
import play.libs.F;
import scala.Option;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.Seq;

public class PlayHeaders implements AgentPropagation.ContextVisitor<Headers> {

  public static final PlayHeaders GETTER = new PlayHeaders();

  @Override
  public void forEachKey(Headers carrier, AgentPropagation.KeyClassifier classifier, AgentPropagation.KeyValueConsumer consumer) {
    for (Tuple2<String, Seq<String>> entry : JavaConversions.asJavaIterable(carrier.data())) {
      String lowerCaseKey = entry._1().toLowerCase();
      int classification = classifier.classify(lowerCaseKey);
      if (classification != -1 && !entry._2().isEmpty()) {
        consumer.accept(classification, lowerCaseKey, entry._2().head());
      }
    }
  }
}

package com.datadog.profiling.controller.openjdk.events;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.FlightRecorder;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.Period;

import java.util.concurrent.atomic.AtomicBoolean;

@Name("datadog.Deadlock")
@Label("Deadlock")
@Description("Datadog deadlock detection event.")
@Category("Datadog")
@Period(value = "57 s")
@Enabled
public class DeadlockEvent extends Event {
  private static final AtomicBoolean REGISTERED_FLAG = new AtomicBoolean();
  private static final DeadlockEventFactory EVENT_FACTORY = new DeadlockEventFactory();

  @Label("Deadlock ID")
  @Description("Referential index for data related to a particular deadlock")
  private final long id;

  @Label("Deadlocked Thread Count")
  private final int threadCount;

  DeadlockEvent() {
    this.id = Long.MIN_VALUE;
    this.threadCount = Integer.MIN_VALUE;
  }

  public DeadlockEvent(long id, int threadCount) {
    this.id = id;
    this.threadCount = threadCount;
  }

  public static void emit() {
    EVENT_FACTORY.collectEvents().forEach(Event::commit);
  }

  public static void register() {
    if (REGISTERED_FLAG.compareAndSet(false, true)) {
      FlightRecorder.addPeriodicEvent(DeadlockEvent.class, DeadlockEvent::emit);
    }
  }

  long getId() {
    return id;
  }

  int getThreadCount() {
    return threadCount;
  }
}

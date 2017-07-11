package com.datadoghq.trace;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Set this annotation to a method so the dd-java-agent considers it for tracing. */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Trace {

  /** The operation name to set. By default it takes the method's name */
  String operationName() default "";
}

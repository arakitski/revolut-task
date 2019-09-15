package com.arakitski.revolut.annotation;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Application server port annotation.
 */
@BindingAnnotation
@Target({PARAMETER, METHOD})
@Retention(RUNTIME)
public @interface ApplicationPort {
}

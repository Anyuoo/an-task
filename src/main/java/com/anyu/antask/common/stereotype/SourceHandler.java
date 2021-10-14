package com.anyu.antask.common.stereotype;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SourceHandler {

    String name();

    Class<?> supClass();

    Class<?> conClass();
}

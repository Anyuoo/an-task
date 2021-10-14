package com.anyu.antask.common.stereotype;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SourceConsumer {

    String name();

    Class<?> conClass();
}

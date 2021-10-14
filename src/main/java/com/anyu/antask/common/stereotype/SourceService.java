package com.anyu.antask.common.stereotype;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SourceService {
    String name() default "";
}

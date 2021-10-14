package com.anyu.antask.common.stereotype;

import java.lang.annotation.*;

/**
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/26 17:16
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SourceCursor {
    String name();

    Class<?> supClass();
}

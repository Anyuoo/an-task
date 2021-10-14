package com.anyu.antask.common.stereotype;

import java.lang.annotation.*;

/**
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/27 15:22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SourceServiceScan {
    String basePackage();
}

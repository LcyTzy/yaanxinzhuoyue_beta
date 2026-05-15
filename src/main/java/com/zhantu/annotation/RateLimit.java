package com.zhantu.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    int time() default 60;
    int count() default 10;
    String key() default "";
}

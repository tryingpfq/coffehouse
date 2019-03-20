package com.tryingpfq.coffeehouse.anno;

import java.lang.annotation.*;

/**
 * @Author Tryingpfq
 * @Time 2019/3/18 22:17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestAnno {
    String value1() default "";

    int id();
}

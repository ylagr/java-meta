package io.github.ylagr.javameta.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author suiwp
 * @date 2025/9/17 10:13
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Meta {

    String aliasName() default "";

}

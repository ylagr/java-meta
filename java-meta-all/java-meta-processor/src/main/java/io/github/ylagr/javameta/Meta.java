package io.github.ylagr.javameta;

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

    public static interface Inner {

    }
}

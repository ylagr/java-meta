package io.github.ylagr.javameta;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author suiwp
 * @date 2025/9/17 13:24
 */
class Hider {

    public static String capitalize(String var0) {
        String var1 = new String(var0.substring(0, 1));
        var1 = var1.toUpperCase();
        return var1 + var0.substring(1);
    }

    public static class GenerateProcessor extends AbstractProcessor {

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return false;
        }

    }
}

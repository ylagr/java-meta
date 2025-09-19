package io.github.ylagr.javameta;

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.ProcessingEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author suiwp
 * @date 2025/9/19 11:11
 */
public class JavacContextHolder {

    static Trees trees;
    static TreeMaker treeMaker;
    static Names names;
    static ProcessingEnvironment processingEnv;

    public static void init(javax.annotation.processing.ProcessingEnvironment processingEnvironment) {
        if (processingEnv != null) {
            return;
        }
        processingEnv = processingEnvironment;
        Class<? extends ProcessingEnvironment> processingEnvClass = processingEnv.getClass();
//        Class<?>[] interfaces = processingEnvClass.getInterfaces();
        //  javax.annotation.processing.ProcessingEnvironment
        //  org.jetbrains.jps.javac.APIWrappers$WrapperDelegateAccessor
//        for (Class<?> anInterface : interfaces) {
//            System.err.println(anInterface.getName());
//        }

        final Context context;

        if (processingEnv instanceof JavacProcessingEnvironment) {
            context = ((JavacProcessingEnvironment) processingEnv).getContext();
        } else if (Proxy.isProxyClass(processingEnvClass)) {
            try {
                Method getWrapperDelegate = processingEnvClass.getDeclaredMethod("getWrapperDelegate");
                Object invoke = getWrapperDelegate.invoke(processingEnv);
                if (invoke instanceof JavacProcessingEnvironment) {
                    processingEnv = (JavacProcessingEnvironment) invoke;
                    context = ((JavacProcessingEnvironment) invoke).getContext();
                } else {
                    throw new IllegalStateException("Not support ProcessingEnvironment: " + processingEnvClass);
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new IllegalStateException("Not support ProcessingEnvironment: " + processingEnvClass, e);
            }
        } else {
            throw new IllegalStateException("Not support ProcessingEnvironment: " + processingEnvClass);
        }

        trees = Trees.instance(processingEnv);
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);

    }
}

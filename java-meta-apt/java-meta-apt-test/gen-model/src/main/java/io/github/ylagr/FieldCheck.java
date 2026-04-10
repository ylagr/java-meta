package io.github.ylagr;

/**
 * @author suiwp
 * @date 2026/4/10 14:37
 */
public interface FieldCheck {
    static <T> void check(T t, io.github.ylagr.javameta.data.IfMetaData<T> metaData){
//        System.out.println(metaData.fieldName);
//        System.out.println(metaData.fieldTypeClass);
//        System.out.println(metaData.fieldTypeClass.isInstance(t));
//        System.out.println(metaData.fieldTypeClass.isAssignableFrom(t.getClass()));
    }
}

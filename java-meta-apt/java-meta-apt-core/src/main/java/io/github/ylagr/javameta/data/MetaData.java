package io.github.ylagr.javameta.data;

/**
 * @author suiwp
 * @date 2026/4/10 14:24
 */
public class MetaData<FieldType> implements io.github.ylagr.javameta.data.IfMetaData<FieldType> {

    public final String fieldName;
    public final Class<?> fieldTypeClass;

    public MetaData(String fieldName,
                    Class<? super FieldType> fieldTypeClass) {
        this.fieldName = fieldName;
        this.fieldTypeClass = fieldTypeClass;
    }
}

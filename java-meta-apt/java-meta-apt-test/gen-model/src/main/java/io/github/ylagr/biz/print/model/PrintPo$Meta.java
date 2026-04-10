package io.github.ylagr.biz.print.model;

import io.github.ylagr.javameta.data.MetaData;

import java.util.List;

/**
 * @author suiwp
 * @date 2026/4/10 14:16
 */
public class PrintPo$Meta {
    public static final PrintPo$Meta INSTANCE = new PrintPo$Meta();
    public final MetaData<String> name = new MetaData<>( "name", String.class);
    public final MetaData<Integer> age = new MetaData<>("age", Integer.class);
    public final MetaData<String> sex = new MetaData<>("sex", String.class);
    public final MetaData<?> value = new MetaData<>( null, null);
    public final MetaData<List<PrintPo<?>>> children = new MetaData<>( "children", List.class);
    public final MetaData<List<String>> emails = new MetaData<>( "emails", List.class);
    public final MetaData<List<Integer>> numbers = new MetaData<>( "numbers", List.class);
}

package io.github.ylagr;

import io.github.ylagr.biz.print.model.PrintPo;



public class TestMeta {
    public static void main(String[] args) {
        // 测试User$Meta类
//        System.out.println("Testing User$Meta...");
//        System.out.println("User.name type: " + User$Meta.INSTANCE.getFieldType("name"));
//        System.out.println("User.age type: " + User$Meta.INSTANCE.getFieldType("age"));
//        System.out.println("User.email type: " + User$Meta.INSTANCE.getFieldType("email"));
//        System.out.println("All field types: " + User$Meta.INSTANCE.getAllFieldTypes());
        System.out.println(MetaAccess.PrintPo.name.fieldName);
        PrintPo<?> po = new PrintPo<>("swp", 18, "male", 18, null);
        FieldCheck.check(po.getAge(), MetaAccess.PrintPo.age);
//        FieldCheck.check(po.getChildren(),MetaAccess.PrintPo.children);
        FieldCheck.check(po.getEmails(),MetaAccess.PrintPo.emails);
        FieldCheck.check(po.getNumbers(),MetaAccess.PrintPo.numbers);
    }

}

package com.example;

import com.example.test.ClassReality;
import com.example.test.School;
import com.example.test.Teacher;

/**
 * @author suiwp
 * @date 2025/9/17 13:47
 */
//@Meta
class GenStudent {
    public final String name;
    private String age;
    public Teacher teacher;
    ClassReality classic;
    School school;

    public GenStudent(String name) {
        this.name = name;
    }

    public void sayHello() {
        System.out.println("Hello from " + this.getClass().getSimpleName());
    }

    public static String getGeneratedValue() {
        return "Generated return value";
    }

    public static class meta {
        public static class name{
            public static  final String named = "name";
            public static final Class< String> typed = String.class;
        }
    }

    public static void main(String[] args) {
        GenStudent ylagr = new GenStudent("ylagr");
        ylagr.sayHello();
//        meta.name.named
        System.out.println(meta.name.typed);
        System.out.println(meta.name.named);
        test(meta.name.named, meta.name.typed, "ylagr" );
//        System.out.println(ylagr.meta.name.named);
    }

    public static <T> boolean test(String sqlField, Class<T> type, T data ) {
        System.out.println("test");
        return type.isInstance(data);
    }
}


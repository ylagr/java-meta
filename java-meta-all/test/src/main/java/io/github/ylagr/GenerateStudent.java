package io.github.ylagr;

import io.github.ylagr.test.ClassReality;
import io.github.ylagr.test.School;
import io.github.ylagr.test.Teacher;

/**
 * @author suiwp
 * @date 2025/9/17 13:47
 */
//@Meta
class GenerateStudent {
    public final String name;
    private String age;
    public Teacher teacher;
    ClassReality classic;
    School school;

    public GenerateStudent(String name) {
        this.name = name;
    }

    public void sayHello() {
        System.out.println("Hello from " + this.getClass().getSimpleName());
    }

    public static String getGeneratedValue() {
        return "Generated return value";
    }

    public static class meta{
        public static class name{
            public static  final String named = "name";
            public static final Class<String> typed = String.class;
        }
    }

    public static void main(String[] args) {
        GenerateStudent ylagr = new GenerateStudent("ylagr");
        ylagr.sayHello();
        System.out.println(meta.name.typed);
        System.out.println(meta.name.named);
        System.out.println(GenStudent.meta.name.named);
    }
}


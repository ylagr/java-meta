package io.github.ylagr;

import io.github.ylagr.javameta.Meta;
import io.github.ylagr.test.ClassReality;
import io.github.ylagr.test.School;
import io.github.ylagr.test.Teacher;

import java.util.List;

@Meta
class Student{
    public Student(String name){
        this.name = name;
    }

    public final String name;
    private String age;
    public Teacher teacher;
    ClassReality classic;
    School school;
//    List<String> nicknames;
//    List<String>[] nickname2;
//    public static class meta{
//        public static class name implements io.github.ylagr.javameta.Meta.Inner{
//
//        }
//    }
}


package com.example;

import io.github.ylagr.javameta.Meta;
import com.example.test.ClassReality;
import com.example.test.School;
import com.example.test.Teacher;

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
    List<String> nicknames;
//    public static class meta{
//        public static class name implements io.github.ylagr.javameta.Meta.Inner{
//
//        }
//    }
}


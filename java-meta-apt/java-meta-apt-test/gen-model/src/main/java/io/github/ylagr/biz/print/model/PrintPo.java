package io.github.ylagr.biz.print.model;

import java.util.List;

/**
 * @author suiwp
 * @date 2026/4/10 09:05
 */

public class PrintPo<T> {
    private String name;
    private Integer age;
    private String sex;
    private T value;
    private List<PrintPo<T>> children;
    private List<String> emails;

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    private List<Integer> numbers;

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public void print() {
    }

    public PrintPo(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public PrintPo(String name, Integer age, String sex, T value, List<PrintPo<T>> children) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.value = value;
        this.children = children;
    }

    public List<PrintPo<T>> getChildren() {
        return children;
    }

    public void setChildren(List<PrintPo<T>> children) {
        this.children = children;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

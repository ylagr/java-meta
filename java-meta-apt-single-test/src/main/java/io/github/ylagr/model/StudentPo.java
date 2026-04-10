package io.github.ylagr.model;

import io.github.ylagr.javameta.annoation.Meta;
import lombok.Data;

/**
 * @author suiwp
 * @date 2026/4/10 16:36
 */
@Data
@Meta(aliasName = "Student$Meta")
public class StudentPo {
    private String name;
    private Integer age;
    private String sex;
    private String address;
    private String phone;
    private String email;
    private String qq;
    private String wechat;
}

package io.github.ylagr.biz.print.model;

import io.github.ylagr.User;
import io.github.ylagr.javameta.annoation.Meta;
import lombok.Data;

import java.util.List;

/**
 * @author suiwp
 * @date 2026/4/10 09:32
 */
@Data
@Meta( )
public class TeacherPo {

    private String name;
    private Integer age;
    private String sex;
    private String address;
    private String phone;
    private String email;
    private String company;
    private String job;
    private String education;
    private String birthday;
    private String hobby;
    private String remark;
    private String photo;
    private String qq;
    private String weixin;
    private String github;
    private String linkedin;
    private String facebook;
    private String twitter;
    private String instagram;
    private String youtube;
    private String tiktok;
    private String zhihu;
    private String douyin;
    private String bilibili;
    private String weibo;
    private String other;
    private String website;
    private String otherContact;
    private String otherContactName;
    private String otherContactPhone;
    private String otherContactEmail;
    private String otherContactAddress;
    private String otherContactWebsite;
    private String otherContactQq;
    private String otherContactWeixin;
    private String otherContactGithub;
    private String otherContactLinkedin;
    private String otherContactFacebook;
    private String otherContactTwitter;

    private List<StudentPo> studentList;
    private List<User> userList;
}

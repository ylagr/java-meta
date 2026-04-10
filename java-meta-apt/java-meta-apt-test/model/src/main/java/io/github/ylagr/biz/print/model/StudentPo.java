package io.github.ylagr.biz.print.model;

import io.github.ylagr.javameta.annoation.Meta;

import java.util.Objects;

/**
 * @author suiwp
 * @date 2026/4/10 09:29
 */

@Meta
public class StudentPo {
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StudentPo)) {
            return false;
        }
        StudentPo studentPo = (StudentPo) o;
        return Objects.equals(name, studentPo.name) && Objects.equals(age, studentPo.age) && Objects.equals(sex, studentPo.sex) && Objects.equals(address, studentPo.address) && Objects.equals(phone, studentPo.phone) && Objects.equals(email, studentPo.email) && Objects.equals(company, studentPo.company) && Objects.equals(job, studentPo.job) && Objects.equals(education, studentPo.education) && Objects.equals(birthday, studentPo.birthday) && Objects.equals(hobby, studentPo.hobby) && Objects.equals(remark, studentPo.remark) && Objects.equals(photo, studentPo.photo) && Objects.equals(qq, studentPo.qq) && Objects.equals(weixin, studentPo.weixin) && Objects.equals(github, studentPo.github) && Objects.equals(linkedin, studentPo.linkedin) && Objects.equals(facebook, studentPo.facebook) && Objects.equals(twitter, studentPo.twitter) && Objects.equals(instagram, studentPo.instagram) && Objects.equals(youtube, studentPo.youtube) && Objects.equals(tiktok, studentPo.tiktok) && Objects.equals(zhihu, studentPo.zhihu) && Objects.equals(douyin, studentPo.douyin) && Objects.equals(bilibili, studentPo.bilibili) && Objects.equals(weibo, studentPo.weibo) && Objects.equals(other, studentPo.other) && Objects.equals(website, studentPo.website) && Objects.equals(otherContact, studentPo.otherContact) && Objects.equals(otherContactName, studentPo.otherContactName) && Objects.equals(otherContactPhone, studentPo.otherContactPhone) && Objects.equals(otherContactEmail, studentPo.otherContactEmail) && Objects.equals(otherContactAddress, studentPo.otherContactAddress) && Objects.equals(otherContactWebsite, studentPo.otherContactWebsite) && Objects.equals(otherContactQq, studentPo.otherContactQq) && Objects.equals(otherContactWeixin, studentPo.otherContactWeixin) && Objects.equals(otherContactGithub, studentPo.otherContactGithub) && Objects.equals(otherContactLinkedin, studentPo.otherContactLinkedin) && Objects.equals(otherContactFacebook, studentPo.otherContactFacebook) && Objects.equals(otherContactTwitter, studentPo.otherContactTwitter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, sex, address, phone, email, company, job, education, birthday, hobby, remark, photo, qq, weixin, github, linkedin, facebook, twitter, instagram, youtube, tiktok, zhihu, douyin, bilibili, weibo, other, website, otherContact, otherContactName, otherContactPhone, otherContactEmail, otherContactAddress, otherContactWebsite, otherContactQq, otherContactWeixin, otherContactGithub, otherContactLinkedin, otherContactFacebook, otherContactTwitter);
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getTiktok() {
        return tiktok;
    }

    public void setTiktok(String tiktok) {
        this.tiktok = tiktok;
    }

    public String getZhihu() {
        return zhihu;
    }

    public void setZhihu(String zhihu) {
        this.zhihu = zhihu;
    }

    public String getDouyin() {
        return douyin;
    }

    public void setDouyin(String douyin) {
        this.douyin = douyin;
    }

    public String getBilibili() {
        return bilibili;
    }

    public void setBilibili(String bilibili) {
        this.bilibili = bilibili;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOtherContact() {
        return otherContact;
    }

    public void setOtherContact(String otherContact) {
        this.otherContact = otherContact;
    }

    public String getOtherContactName() {
        return otherContactName;
    }

    public void setOtherContactName(String otherContactName) {
        this.otherContactName = otherContactName;
    }

    public String getOtherContactPhone() {
        return otherContactPhone;
    }

    public void setOtherContactPhone(String otherContactPhone) {
        this.otherContactPhone = otherContactPhone;
    }

    public String getOtherContactEmail() {
        return otherContactEmail;
    }

    public void setOtherContactEmail(String otherContactEmail) {
        this.otherContactEmail = otherContactEmail;
    }

    public String getOtherContactAddress() {
        return otherContactAddress;
    }

    public void setOtherContactAddress(String otherContactAddress) {
        this.otherContactAddress = otherContactAddress;
    }

    public String getOtherContactWebsite() {
        return otherContactWebsite;
    }

    public void setOtherContactWebsite(String otherContactWebsite) {
        this.otherContactWebsite = otherContactWebsite;
    }

    public String getOtherContactQq() {
        return otherContactQq;
    }

    public void setOtherContactQq(String otherContactQq) {
        this.otherContactQq = otherContactQq;
    }

    public String getOtherContactWeixin() {
        return otherContactWeixin;
    }

    public void setOtherContactWeixin(String otherContactWeixin) {
        this.otherContactWeixin = otherContactWeixin;
    }

    public String getOtherContactGithub() {
        return otherContactGithub;
    }

    public void setOtherContactGithub(String otherContactGithub) {
        this.otherContactGithub = otherContactGithub;
    }

    public String getOtherContactLinkedin() {
        return otherContactLinkedin;
    }

    public void setOtherContactLinkedin(String otherContactLinkedin) {
        this.otherContactLinkedin = otherContactLinkedin;
    }

    public String getOtherContactFacebook() {
        return otherContactFacebook;
    }

    public void setOtherContactFacebook(String otherContactFacebook) {
        this.otherContactFacebook = otherContactFacebook;
    }

    public String getOtherContactTwitter() {
        return otherContactTwitter;
    }

    public void setOtherContactTwitter(String otherContactTwitter) {
        this.otherContactTwitter = otherContactTwitter;
    }

}

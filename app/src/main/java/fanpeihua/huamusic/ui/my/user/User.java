package fanpeihua.huamusic.ui.my.user;

import java.io.Serializable;

public class User implements Serializable {
    private String id = ""; //id
    private String name = ""; //姓名
    private String sex = ""; //性别

    private String avatar = "";   //头像
    private String email = "";  //邮箱
    private String phone = "";  //手机号
    private String nickname = "";  //昵称
    private String token;//访问令牌
    private int secret = 0;  //用户是否保密

    public User() {
    }

    public User(String id, String name, String sex, String avatar, String email, String phone, String nickname, String token, int secret) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.avatar = avatar;
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
        this.token = token;
        this.secret = secret;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getSecret() {
        return secret;
    }

    public void setSecret(int secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", token='" + token + '\'' +
                ", secret=" + secret +
                '}';
    }
}

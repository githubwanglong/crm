package com.wang.crm.user.query;

import com.wang.crm.base.BaseQuery;

public class UserQuery extends BaseQuery {
    private String userName;    //用户名
    private String email;   //邮箱
    private String phone;   //手机号码
    private String trueName;//真实姓名

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}

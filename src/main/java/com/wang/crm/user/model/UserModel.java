package com.wang.crm.user.model;

/**
 * 用户模块
 */
public class UserModel {

    private Integer id;
    private String userName;
    private String userPwd;
    private String trueName;

    public UserModel() {
    }

    public UserModel(Integer id, String userName, String userPwd, String trueName) {
        this.id = id;
        this.userName = userName;
        this.userPwd = userPwd;
        this.trueName = trueName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer idStr) {
        id = idStr;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }
}



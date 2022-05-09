package com.wang.crm.exceptions;

/**
 * 自定义权限异常
 */
public class AuthException extends RuntimeException {
    private Integer code=400;
    private String msg="你还没有权限哦!";


    public AuthException() {
        super("你还没有权限哦!");
    }

    public AuthException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public AuthException(Integer code) {
        super("你还没有权限哦!");
        this.code = code;
    }

    public AuthException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

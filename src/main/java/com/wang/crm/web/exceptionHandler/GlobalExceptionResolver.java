package com.wang.crm.web.exceptionHandler;

import com.wang.crm.base.ResultInfo;
import com.wang.crm.exceptions.AuthException;
import com.wang.crm.exceptions.NoLoginException;
import com.wang.crm.exceptions.ParamsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 全局异常处理器
 */
@ControllerAdvice
public class GlobalExceptionResolver {

    /**
     * 参数异常
     * @param p 参数异常异常类class
     * @return  处理结果对象
     */
    @ResponseBody
    @ExceptionHandler(value = ParamsException.class)
    public ResultInfo paramException(ParamsException p){
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setCode(p.getCode());
        resultInfo.setMsg(p.getMsg());
        return resultInfo;
    }


    /**
     * 未登录异常
     * @param n 未登录异常类class
     * @return  登录页面处理器方法
     */
    @ExceptionHandler(value = NoLoginException.class)
    public String noLoginException(NoLoginException n){
        //转发到登录页面的处理器方法
        return "index";
    }


    /**
     * 认证异常
     * @param p 认证异常类class
     * @return  处理结果对象
     */
    @ExceptionHandler(value = AuthException.class)
    public String AuthException(AuthException p){
        return "authException";
    }


    /**
     * 其它异常
     * @param e 异常类class
     * @return  处理结果对象
     *//*
    @ResponseBody
    @ExceptionHandler()
    public ResultInfo otherException(Exception e){
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setCode(300);
        resultInfo.setMsg("出错了!");
        return resultInfo;
    }*/
}

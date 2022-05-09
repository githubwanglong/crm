package com.wang.crm.web.interceptor;

import com.wang.crm.exceptions.NoLoginException;
import com.wang.crm.user.model.UserModel;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 非法访问拦截器
 */
public class NoLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        //获取session中的user
        UserModel userModel = (UserModel) request.getSession().getAttribute("user");
        //判断用户是否登录
        if (null == userModel){
            //抛出未登录异常，在全局异常中统一处理
            throw new NoLoginException();
        }
        //已登录
        return true;
    }
}

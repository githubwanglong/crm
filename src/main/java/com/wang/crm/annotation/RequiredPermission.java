package com.wang.crm.annotation;


import java.lang.annotation.*;


/**
 * 权限认证的注解
 *      在需要权限才能使用的功能上方加入此注解并添加权限码
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredPermission {

    //权限码
    String code();
}

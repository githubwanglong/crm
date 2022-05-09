package com.wang.crm.aspect;

import com.wang.crm.annotation.RequiredPermission;
import com.wang.crm.exceptions.AuthException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 权限验证的切面类
 */
@Component
@Aspect
public class PermissionProxy {

    @Resource
    private HttpSession session;

    /**
     * 这个切面方法会拦截RequiredPermission注解
     *  如果通知方法上加了此注解会判断当前登录用户所有用的权限中是否包含RequiredPermission注解中的权限码。
     * @param pjp
     * @return
     */
    @Around(value = "@annotation(com.wang.crm.annotation.RequiredPermission)")
    public Object permissionVerify(ProceedingJoinPoint pjp) throws Throwable {
        Object obj = null;
        //得到当前用户所有用的权限。
        List<String> permissions = (List<String>) session.getAttribute("permissions");
        //判断用户是否有权限
        if (null == permissions || permissions.size() < 1){
            //抛出认证异常
            throw new AuthException();
        }

        //得到目标方法
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        //获取目标方法上的注解
        RequiredPermission requiredPermission = methodSignature.getMethod().getAnnotation(RequiredPermission.class);
        //获取注解的code属性的值
        String code = requiredPermission.code();
        //判断当前登录用户是否有该权限
        if (!permissions.contains(code)){
            //抛出认证异常
            throw new AuthException();
        }
        obj = pjp.proceed();
        return obj;
    }
}

package com.wang.crm.web.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 非法访问拦截器的配置类
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        NoLoginInterceptor noLoginInterceptor = new NoLoginInterceptor();
        registry.addInterceptor(noLoginInterceptor).
                addPathPatterns("/**").
                excludePathPatterns("/index","/user/login","/css/**","/images/**","/js/**","/lib/**","/main");
    }
}

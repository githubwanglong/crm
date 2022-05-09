package com.wang.crm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = {"com.wang.crm"})//扫描所有dao类
@EnableTransactionManagement    //启用事务
@EnableScheduling   //启用定时任务
public class CrmApplication extends SpringBootServletInitializer{

    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
    }

    /*web项目的启动入口*/
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CrmApplication.class);
    }
}

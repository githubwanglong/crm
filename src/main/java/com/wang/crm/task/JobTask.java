package com.wang.crm.task;

import com.wang.crm.customer.service.CustomerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时任务
 */
@Component
public class JobTask {

    @Resource
    private CustomerService customerService;


    //@Scheduled(cron = "0/2 * * * * ?")
    public void job(){
        //调用需要被执行的方法
        customerService.updateCustomerState();
    }
}

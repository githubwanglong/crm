package com.wang.crm.report.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("report")
public class ReportController {


    /**
     * 进入统计报表视图
     * @param type
     * @return
     */
    @RequestMapping("{type}")
    public String index(@PathVariable Integer type){
        //判断统计报表的类型
        if (type != null){
            if (type == 0){//客户贡献分析
                return "report/customer_contri";
            }else if (type == 1){//客户构成分析
                return "report/customer_make";
            }else if (type == 2){//客户服务分析
                return "report/customer_loss";
            }
        }
        return "";
    }

}

package com.wang.crm.customer.controller;


import com.wang.crm.annotation.RequiredPermission;
import com.wang.crm.base.BaseController;
import com.wang.crm.base.ResultInfo;
import com.wang.crm.customer.domain.CustomerLoss;
import com.wang.crm.customer.query.CustomerLossQuery;
import com.wang.crm.customer.service.CustomerLossService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping(value = "customer_loss")
@Controller
public class CustomerLossController extends BaseController {

    @Resource
    private CustomerLossService customerLossService;


    /**
     * 进入客户流失视图
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "customerLoss/customer_loss";
    }


    /**
     * 多条件分页查询流失客户
     * @param customerLossQuery
     * @return
     */
    @ResponseBody
    @GetMapping("list")
    public Map<String, Object> queryLossCustomerByParams(CustomerLossQuery customerLossQuery){
        return customerLossService.queryLossCustomerByParams(customerLossQuery);
    }


    /**
     * 打开添加暂缓/暂缓措施查看视图
     * @return
     */
    @RequestMapping("toCustomerLossPage")
    public String toCustomerLossPage(HttpServletRequest request, Integer lossId){
        //根据流失客户id查询流失客户
        CustomerLoss customerLoss = customerLossService.selectByPrimaryKey(lossId);
        //将流失客户信息放入作用域中供前台获取
        request.setAttribute("customerLoss", customerLoss);
        return "customerLoss/customer_rep";
    }


    /**
     * 更新客户的流失状态
     * @param customerLoss
     * @return
     */
    @RequiredPermission(code = "202004")
    @PostMapping("updateCustomerLossStateById")
    @ResponseBody
    public ResultInfo updateCustomerLossStateById(CustomerLoss customerLoss){
        customerLossService.updateCustomerLossStateById(customerLoss);
        return success("确认流失成功!");
    }

    /**
     * 将流失客户恢复正常
     * @param customerLoss
     * @return
     */
    @RequiredPermission(code = "202005")
    @PostMapping("renewCustomerLossStateById")
    @ResponseBody
    public ResultInfo renewCustomerLossStateById(CustomerLoss customerLoss){
        customerLossService.renewCustomerLossStateById(customerLoss);
        return success("恢复流失客户成功!");
    }
}

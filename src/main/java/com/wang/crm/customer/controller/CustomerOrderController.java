package com.wang.crm.customer.controller;

import com.wang.crm.annotation.RequiredPermission;
import com.wang.crm.base.BaseController;
import com.wang.crm.customer.domain.CustomerOrder;
import com.wang.crm.customer.query.CustomerOrderQuery;
import com.wang.crm.customer.service.CustomerOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@RequestMapping("order")
@Controller
public class CustomerOrderController extends BaseController {

    @Resource
    private CustomerOrderService customerOrderService;


    /**
     * 多条件分页查询客户订单列表
     * @param customerOrderQuery
     * @return
     */
    @RequiredPermission(code = "102004")
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryCustomerOrderByParams(CustomerOrderQuery customerOrderQuery){
        return customerOrderService.queryCustomerOrderByParams(customerOrderQuery);
    }


    /**
     * 进入订单详情页面
     * @param orderId
     * @return
     */
    @RequestMapping("openOrderDetailPage")
    public String openOrderDetailPage(Integer orderId, Model model){
        //根据订单id查询详情放入请求作用域中
        Map<String, Object> map = customerOrderService.selectOrderById(orderId);
        model.addAttribute("order", map);

        return "customer/customer_order_detail";
    }
}

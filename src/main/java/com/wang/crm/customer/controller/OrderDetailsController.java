package com.wang.crm.customer.controller;

import com.wang.crm.base.BaseController;
import com.wang.crm.customer.query.OrderDetailsQuery;
import com.wang.crm.customer.service.OrderDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("order_details")
public class OrderDetailsController extends BaseController {

    @Resource
    private OrderDetailsService orderDetailsService;


    /**
     * 根据订单id查询订单详情（多条件分页查询）
     * @param orderDetailsQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryOrderDetailsByOrderId(OrderDetailsQuery orderDetailsQuery){
        return orderDetailsService.queryOrderDetailsByOrderId(orderDetailsQuery);
    }
}

package com.wang.crm.customer.dao;

import com.wang.crm.customer.domain.CustomerOrder;
import com.wang.crm.base.BaseMapper;

import java.util.Map;

public interface CustomerOrderMapper extends BaseMapper<CustomerOrder, Integer> {

    /**
     * 通过订单id查询订单记录
     * @param orderId
     * @return
     */
    Map<String, Object> selectOrderById(Integer orderId);


    /**
     * 通过客户id查询客户最后一次的订单记录
     * @param customerId
     * @return
     */
    CustomerOrder queryLossCustomerOrderByCustomerId(Integer customerId);
}
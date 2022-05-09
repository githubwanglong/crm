package com.wang.crm.customer.dao;

import com.wang.crm.base.BaseMapper;
import com.wang.crm.customer.domain.Customer;
import com.wang.crm.customer.query.CustomerQuery;

import java.util.List;
import java.util.Map;


public interface CustomerMapper extends BaseMapper<Customer, Integer> {

    /**
     * 根据客户名称查询客户
     * @param customerName
     * @return
     */
    Customer queryCustomerByName(String customerName);


    /**
     * 查询待流失的客户数据
     *      判定流失条件：
     *          客户自创建起，超过六个月没有产生订单记录，或客户最后下单日期距离现在超过六个月
     * @return
     */
    List<Customer> queryLossCustomers();

    //批量更新流失状态
    int updateCustomerStateById(List<Integer> lossCusIdList);


    //多条件查询客户贡献数据
    List<Map<String, Object>> queryCustomerContributionByParams(CustomerQuery customerQuery);


    //查询客户构成数据
    List<Map<String, Object>> queryCustomerMake();
}
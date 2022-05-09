package com.wang.crm.customer.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.crm.base.BaseService;
import com.wang.crm.customer.dao.CustomerOrderMapper;
import com.wang.crm.customer.domain.CustomerOrder;
import com.wang.crm.customer.query.CustomerOrderQuery;
import com.wang.crm.customer.query.CustomerQuery;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerOrderService extends BaseService<CustomerOrder, Integer> {

    @Resource
    private CustomerOrderMapper customerOrderMapper;

    /**
     *多条件分页查询客户订单信息。(返回的数据格式必须满足LayUi中数据表格要求的格式)
     * @param customerOrderQuery
     * @return
     */
    public Map<String, Object> queryCustomerOrderByParams(CustomerOrderQuery customerOrderQuery) {
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerOrderQuery.getPage(), customerOrderQuery.getLimit());
        //得到对应分页对象
        PageInfo<CustomerOrder> pageInfo = new PageInfo(customerOrderMapper.selectByParams(customerOrderQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //需要分页的数据
        map.put("data", pageInfo.getList());
        return map;

    }

    /**
     * 根据id查询记录详情
     * @param orderId
     * @return
     */
    public Map<String, Object> selectOrderById(Integer orderId) {
        return customerOrderMapper.selectOrderById(orderId);
    }
}

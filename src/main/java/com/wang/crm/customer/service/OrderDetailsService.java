package com.wang.crm.customer.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.crm.base.BaseService;
import com.wang.crm.customer.dao.OrderDetailsMapper;
import com.wang.crm.customer.domain.OrderDetails;
import com.wang.crm.customer.query.CustomerOrderQuery;
import com.wang.crm.customer.query.OrderDetailsQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderDetailsService extends BaseService<OrderDetails, Integer> {

    @Resource
    private OrderDetailsMapper orderDetailsMapper;

    /**
     * 根据订单id查询订单详情（多条件分页查询）
     * @param orderDetailsQuery
     * @return
     */
    public Map<String, Object> queryOrderDetailsByOrderId(OrderDetailsQuery orderDetailsQuery) {
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(orderDetailsQuery.getPage(), orderDetailsQuery.getLimit());
        //得到对应分页对象
        PageInfo<OrderDetails> pageInfo = new PageInfo<>(orderDetailsMapper.selectByParams(orderDetailsQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //需要分页的数据
        map.put("data", pageInfo.getList());
        return map;
    }
}

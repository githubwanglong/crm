package com.wang.crm.customer.query;

import com.wang.crm.base.BaseQuery;

public class OrderDetailsQuery extends BaseQuery {

    private Integer orderId;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}

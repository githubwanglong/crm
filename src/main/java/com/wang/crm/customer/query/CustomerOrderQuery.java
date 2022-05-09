package com.wang.crm.customer.query;

import com.wang.crm.base.BaseQuery;

public class CustomerOrderQuery extends BaseQuery {

    private Integer cusId;//客户id

    public Integer getCusId() {
        return cusId;
    }

    public void setCusId(Integer customerId) {
        this.cusId = customerId;
    }
}

package com.wang.crm.salemanager.query;

import com.wang.crm.base.BaseQuery;

/**
 * 客户开发计划项数据查询类
 */
public class CusDevPlanQuery extends BaseQuery {

    //分页参数从BaseQuery中继承

    private Integer saleChanceId;   //营销机会的主键 id

    public Integer getSaleChanceId() {
        return saleChanceId;
    }

    public void setSaleChanceId(Integer saleChanceId) {
        this.saleChanceId = saleChanceId;
    }
}

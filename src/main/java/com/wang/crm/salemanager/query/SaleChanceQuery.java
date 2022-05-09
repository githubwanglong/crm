package com.wang.crm.salemanager.query;

import com.wang.crm.base.BaseQuery;

/**
 * 营销机会查询类
 */
public class SaleChanceQuery extends BaseQuery {

    //分页参数从BaseQuery中继承

    //营销机会条件查询参数
    private String customerName;//客户名称
    private String createMan;//创建人
    private Integer state;//分配状态 0=未分配 1=已分配
    private Integer devResult;//开发状态 0=未开发 1=开发中 2=开发成功 3=开发失败


    //客户开发计划条件查询参数，客户名称、创建人、开发状态上方已定义
    private Integer assignMan;   //分配人


    public SaleChanceQuery() {
    }

    public SaleChanceQuery(String customerName, String createMan, Integer state, Integer devResult) {
        this.customerName = customerName;
        this.createMan = createMan;
        this.state = state;
        this.devResult = devResult;
    }

    public Integer getAssignMan() {
        return assignMan;
    }

    public void setAssignMan(Integer assignMan) {
        this.assignMan = assignMan;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreateMan() {
        return createMan;
    }

    public void setCreateMan(String createMan) {
        this.createMan = createMan;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getDevResult() {
        return devResult;
    }

    public void setDevResult(Integer devResult) {
        this.devResult = devResult;
    }
}

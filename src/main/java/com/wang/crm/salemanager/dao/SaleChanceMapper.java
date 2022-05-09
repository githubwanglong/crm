package com.wang.crm.salemanager.dao;

import com.wang.crm.base.BaseMapper;
import com.wang.crm.salemanager.domain.SaleChance;

public interface SaleChanceMapper extends BaseMapper<SaleChance, Integer> {
    /**
     * 多条件查询的方法不需要单独定义
     * 由于多个模块中涉及到多条件查询操作，所以将多条件查询功能定义在父接口BaseMapper中
     */


}
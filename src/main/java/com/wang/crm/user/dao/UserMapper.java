package com.wang.crm.user.dao;

import com.wang.crm.base.BaseMapper;
import com.wang.crm.user.domain.User;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User, Integer> {

    //根据用户名和密码查询用户，返回用户对象。
    User queryUserByNameAndPassword(String userName, String userPwd);

    /**
     * 查询所有销售人员
     * @return
     */
    List<Map<String, Object>> queryAllSales();

    /**
     * 根据userName查询用户
     * @param userName
     * @return
     */
    User selectByUserName(String userName);


    /**
     * 查询所有客户经理
     * @return
     */
    List<Map<String, Object>> queryCustomerManagers();
}
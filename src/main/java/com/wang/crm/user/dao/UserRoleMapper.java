package com.wang.crm.user.dao;


import com.wang.crm.base.BaseMapper;
import com.wang.crm.user.domain.UserRole;

/**
 * 用户角色dao
 */
public interface UserRoleMapper extends BaseMapper<UserRole, Integer> {

    //查询用户绑定的角色数量
    Integer countUserRoleByUserId(Integer userId);

    //删除用户绑定的角色
    Integer deleteUserRoleByUserId(Integer userId);
}
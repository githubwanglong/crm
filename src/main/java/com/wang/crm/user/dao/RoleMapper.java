package com.wang.crm.user.dao;


import com.wang.crm.base.BaseMapper;
import com.wang.crm.user.domain.Role;

import java.util.List;
import java.util.Map;

/**
 * 角色Dao类
 */
public interface RoleMapper extends BaseMapper<Role, Integer> {

    //查询所有角色(只需要id与roleName所以使用map)
    List<Map<String, Object>> queryAllRoles(Integer userId);

    //根据角色名称查询角色信息
    Role selectRoleByRoleName(String roleName);
}
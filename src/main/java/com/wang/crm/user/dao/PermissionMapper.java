package com.wang.crm.user.dao;

import com.wang.crm.base.BaseMapper;
import com.wang.crm.user.domain.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission, Integer> {

    //通过角色id查询该角色权限记录条数
    Integer countSelectByRoleId(Integer roleId);


    //根据角色id删除权限记录
    Integer deletePermissionByRoleId(Integer roleId);

    //通过角色id查询该角色已拥有的资源id
    List<Integer> queryRoleHasModuleIdsByRoleId(Integer roleId);


    //根据角色id查询拥有的角色，角色拥有的资源，得到用户拥有的资源列表（资源权限码）
    List<String> queryUserHasRoleHasPermission(Integer userId);

    //根据资源id查询权限
    Integer countPermissionByModuleId(Integer moduleId);

    //根据资源id删除权限
    Integer deletePermissionByModuleId(Integer moduleId);
}
package com.wang.crm.user.service;

import com.wang.crm.base.BaseService;
import com.wang.crm.user.dao.PermissionMapper;
import com.wang.crm.user.domain.Permission;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;


@Service
public class PermissionService extends BaseService<Permission, Integer> {

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 根据角色id查询拥有的角色，角色拥有的资源，得到用户拥有的资源列表（资源权限码）
     * @param userId
     * @return
     */
    public List<String> queryUserHasRoleHasPermission(Integer userId) {
        return permissionMapper.queryUserHasRoleHasPermission(userId);
    }

}

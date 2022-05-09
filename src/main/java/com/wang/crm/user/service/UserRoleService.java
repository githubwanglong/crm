package com.wang.crm.user.service;

import com.wang.crm.base.BaseService;
import com.wang.crm.user.dao.UserRoleMapper;
import com.wang.crm.user.domain.UserRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户角色业务逻辑层
 */
@Service
public class UserRoleService extends BaseService<UserRole, Integer> {

    @Resource
    private UserRoleMapper userRoleMapper;
}

package com.wang.crm.user.query;

import com.wang.crm.base.BaseQuery;

/**
 * 角色查询类
 */
public class RoleQuery extends BaseQuery {

    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

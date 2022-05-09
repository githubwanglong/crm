package com.wang.crm.user.web.controller;

import com.wang.crm.base.BaseController;
import com.wang.crm.user.service.UserRoleService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * 用户角色控制器
 */
@Controller
public class UserRoleController extends BaseController{

    @Resource
    private UserRoleService userRoleService;
}

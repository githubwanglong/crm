package com.wang.crm.user.web.controller;


import com.wang.crm.annotation.RequiredPermission;
import com.wang.crm.base.BaseController;
import com.wang.crm.base.ResultInfo;
import com.wang.crm.user.domain.Role;
import com.wang.crm.user.query.RoleQuery;
import com.wang.crm.user.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 角色controller
 */
@Controller
@RequestMapping("role")
public class RoleController extends BaseController {

    @Resource
    private RoleService roleService;


    /**
     * 查询所有角色
     * @return
     */
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String, Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }


    /**
     * 多条件分页查询
     * @param roleQuery
     * @return
     */
    @RequiredPermission(code = "602002")
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> selectByParams(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }


    /**
     * 进入角色管理页面
     * @return
     */
    @RequiredPermission(code = "6020")
    @RequestMapping("index")
    public String index(){
        return "role/role";
    }


    /**
     * 角色添加
     * @param role
     * @return
     */
    @RequiredPermission(code = "602001")
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addRole(Role role){
        roleService.addRole(role);
        return success("添加角色成功!");
    }


    /**
     * 进入添加/修改角色的视图
     * @return
     */
    @RequestMapping("toAddOrUpdateRolePage")
    public String toRolePage(HttpServletRequest request, Integer roleId){
        //判断id是否为空，如果id不为空则是更新操作
        if (roleId != null){//更新操作
            Role role = roleService.selectByPrimaryKey(roleId);
            //根据id查询角色，将角色信息放入到作用域中供前台获取。
            request.setAttribute("role", role);
        }
        return "role/add_update";
    }


    /**
     * 更新角色
     * @param role
     * @return
     */
    @RequiredPermission(code = "602003")
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("更新角色成功!");
    }


    /**
     * 删除角色
     * @param roleId
     * @return
     */
    @RequiredPermission(code = "602004")
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer roleId){
        roleService.deleteRole(roleId);
        return success("删除角色成功!");
    }


    /**
     * 角色授权
     * @param moduleIds
     * @param roleId
     * @return
     */
    @RequiredPermission(code = "602005")
    @PostMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer[] moduleIds, Integer roleId){

        roleService.addGrant(moduleIds, roleId);
        return success("角色授权成功!");
    }
}

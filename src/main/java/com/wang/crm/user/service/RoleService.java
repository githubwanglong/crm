package com.wang.crm.user.service;

import com.wang.crm.base.BaseService;
import com.wang.crm.user.dao.ModuleMapper;
import com.wang.crm.user.dao.PermissionMapper;
import com.wang.crm.user.dao.RoleMapper;
import com.wang.crm.user.domain.Permission;
import com.wang.crm.user.domain.Role;
import com.wang.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 角色service类
 */
@Service
public class RoleService extends BaseService<Role, Integer> {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private ModuleMapper moduleMapper;


    /**
     * 查询所有角色
     * @return
     */
    public List<Map<String, Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }


    /**
     * 添加角色
     *      1、参数验证
     *          角色名称    非空且唯一
     *
     *      2、设置默认参数
     *          是否有效    1
     *          创建时间    系统当前时间
     *
     *      3、执行添加操作并判断执行结果
     * @param role
     */
    @Transactional
    public void addRole(Role role){
        //1、参数验证
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "角色名称不能为空");
        AssertUtil.isTrue(roleMapper.selectRoleByRoleName(role.getRoleName()) != null, "角色名称已存在，请重新输入!");

        //2、设置默认参数
        role.setIsValid(1);
        role.setCreateDate(new Date());

        //3、执行添加操作并判断执行结果
        AssertUtil.isTrue(roleMapper.insertSelective(role) != 1, "添加角色失败!");
    }


    /**
     * 更新角色
     *      1、参数判断
     *          角色id    非空且数据存在
     *          角色名称    非空且名称唯一
     *      2、设置默认参数
     *          最近更新时间  系统当前时间
     *      3、执行更新操作并判断执行结果
     * @param role
     */
    @Transactional
    public void updateRole(Role role){
        //1、参数判断
        AssertUtil.isTrue(role.getId() == null || roleMapper.selectByPrimaryKey(role.getId()) == null, "待更新角色不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "角色名称不能为空!");
        Role temp = roleMapper.selectRoleByRoleName(role.getRoleName());

        /*
            如果查询出来的角色信息为空，那么角色名称可以使用
            如果查询出来的角色信息不为空，但id与传递过来的id一致说明待更新的记录和查询出来的记录是同一条，角色名称可以一样
         */
        AssertUtil.isTrue(temp != null && !temp.getId().equals(role.getId()), "角色名称已存在，请重新输入!");

        //2、设置默认参数
        role.setUpdateDate(new Date());

        //执行更新操作并判断执行结果
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) != 1, "角色更新失败!");
    }


    /**
     * 删除角色(逻辑删除，将is_valid字段更改为0)
     *      1、参数校验
     *          id不为空且数据存在
     *      2、更改参数
     *          is_valid    0
     *          update_date 当前系统时间
     *      3、执行更新操作并判断执行结果
     * @param roleId
     */
    @Transactional
    public void deleteRole(Integer roleId){
        //判断角色id是否为空
        Role role = roleMapper.selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null == roleId || null == role, "待删除角色不存在!");
        //设置参数
        role.setIsValid(0);
        role.setUpdateDate(new Date());
        //执行删除操作并判断执行结果
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) != 1, "删除角色失败!");
    }


    /**
     * 角色授权操作
     *
     *      将角色id和资源id添加到权限表中
     *      若是直接添加权限会出现重复的权限数据，因为每次进行勾选时都会将以前选中的也传递过来，
     *      应该先将当前角色的所有权限删除后再进行添加权限添加。
     *          1、通过角色id查询对应的权限记录。
     *          2、如果有权限记录存在，则删除后再进行添加，若是没有当前角色没有权限记录存在则直接添加。
     *
     * @param moduleIds
     * @param roleId
     */
    @Transactional
    public void addGrant(Integer[] moduleIds, Integer roleId) {
        //参数校验，判断角色id是否为空且数据存在
        AssertUtil.isTrue(roleId == null, "角色不存在!");
        //根据id查询角色，判断角色是否存在
        AssertUtil.isTrue(roleMapper.selectByPrimaryKey(roleId) == null, "角色不存在!");
        //通过角色id查询角色的所有权限记录
        Integer count = permissionMapper.countSelectByRoleId(roleId);
        if (count > 0){
            //删除当前角色的所有权限，并判断执行结果
            AssertUtil.isTrue(!count.equals(permissionMapper.deletePermissionByRoleId(roleId)), "授权失败!");
        }
        //判断是否有权限需要添加
        if (moduleIds != null && moduleIds.length > 0){

            List<Permission> list = new ArrayList<>();

            //将资源id取出放入实体类中，并设置默认参数
            for (Integer moduleId : moduleIds){
                Permission permission = new Permission();
                permission.setModuleId(moduleId);
                permission.setRoleId(roleId);
                permission.setCreateDate(new Date());
                //授权码，从资源表中获取
                permission.setAclValue(moduleMapper.selectByPrimaryKey(moduleId).getOptValue());

                //将对象放入集合中
                list.add(permission);
            }
            //执行批量添加，并判断执行结果
            AssertUtil.isTrue(permissionMapper.insertBatch(list) != list.size(), "授权失败!");
        }

    }
}

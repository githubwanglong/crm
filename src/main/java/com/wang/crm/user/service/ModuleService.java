package com.wang.crm.user.service;

import com.wang.crm.base.BaseService;
import com.wang.crm.user.dao.ModuleMapper;
import com.wang.crm.user.dao.PermissionMapper;
import com.wang.crm.user.domain.Module;
import com.wang.crm.user.domain.Permission;
import com.wang.crm.user.model.TreeModel;
import com.wang.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module, Integer> {

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 查询所有资源列表，供前台zTree使用
     * @return
     */
    public List<TreeModel> queryAllModules(Integer roleId){
        //查询所有的资源列表
        List<TreeModel> treeModelList = moduleMapper.queryAllModules();

        //查询角色已经授权的资源列表（角色已经拥有的资源id）
        List<Integer> permissionIds = permissionMapper.queryRoleHasModuleIdsByRoleId(roleId);
        if (permissionIds != null && permissionIds.size() > 0){
            //遍历所有的资源列表，判断用户拥有的资源列表中是否有匹配的，如果有则将checked设置为true。
            for (TreeModel treeModel : treeModelList){
                if (permissionIds.contains(treeModel.getId())){
                    treeModel.setChecked(true);
                }
            }
        }
        return treeModelList;
    }


    /**
     * 查询所有资源数据，供前台表格使用
     * @return
     */
    public Map<String, Object> queryModuleList(){
        Map<String, Object> map = new HashMap<>();
        List<Module> modules = moduleMapper.queryModuleList();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", modules.size());
        map.put("data", modules);

        return map;
    }

    /**
     * 添加资源
     *      1、参数校验
     *          模块名称moduleName
     *              非空且同父菜单id下模块名称唯一
     *
     *          地址url
     *              二级菜单（grade==1）非空且不可重复
     *
     *          父级菜单parentId
     *              一级（目录）（grade==0）  -1
     *              二级|三级（菜单|按钮）（grade==1或2）    非空且父级菜单必须存在
     *
     *          层级grade
     *              非空   0|1|2
     *
     *          权限码optValue
     *              非空，不可重复
     *      2、设置默认默认值
     *          是否有效is_valid    1
     *          创建时间createDate  系统当前时间
     *
     *      3、执行添加操作并判断执行结果
     *
     * @param module
     */
    @Transactional
    public void addModule(Module module){
        //1、参数校验模块名称moduleName
        //层级grade
        //    非空   0|1|2
        Integer grade = module.getGrade();
        AssertUtil.isTrue(grade == null || !(grade == 0 || grade == 1 || grade == 2), "层级只能为0、1、2");

        // 模块名称moduleName，非空且同父菜单id下模块名称唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "模块名称不能为空!");
        AssertUtil.isTrue(moduleMapper.selectByGradeAndModuleNameAndParentId(grade, module.getModuleName(), module.getParentId()) != null,
                "模块名称已存在!");

        //地址url，二级菜单（grade==1）非空且不可重复
        if (grade == 1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "当层级为1时URL不可为空");
            AssertUtil.isTrue(moduleMapper.selectByGradeAndUrl(grade, module.getUrl()) != null, "URL已存在!");
        }

        //父级菜单parentId
        //    一级（目录）（grade==0）  null
        //    二级|三级（菜单|按钮）（grade==1或2）    非空且父级菜单必须存在
        if (grade == 0){
            AssertUtil.isTrue(module.getParentId() != -1, "层级为0时父级菜单需为-1!");
        }else{
            AssertUtil.isTrue(null == module.getParentId(), "层级不为0时必须有父级菜单!");
            AssertUtil.isTrue(moduleMapper.selectByPrimaryKey(module.getParentId()) == null, "请指定正确的父级菜单!");
        }

        //权限码optValue 非空且不可重复
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不能为空!");
        AssertUtil.isTrue(moduleMapper.selectByOptValue(module.getOptValue()) != null, "权限码已存在!");

        //2、设置默认参数
        module.setIsValid((byte) 1);
        module.setCreateDate(new Date());

        //执行添加操作并
        AssertUtil.isTrue(moduleMapper.insertSelective(module) != 1, "添加资源失败!");
    }


    /**
     * 更新资源
     *  1、参数校验
     *      id  非空且数据存在
     *      grade   非空0|1|2
     *      moduleName  非空且同一层级下模块名称唯一（不包含当前记录本身）
     *      url     二级菜单（grade=1），非空且同一层级下不可重复（不包含当前记录本身）
     *      optValue    非空且不可重复（不包含当前记录本身）
     *
     *  2、设置参数的默认值
     *      updateDate  当前系统时间
     *
     *  3、执行更新操作，判断受影响的行数
     * @param module
     */
    @Transactional
    public void updateModule(Module module){
        //1、参数校验
        //id  非空且数据存在
        AssertUtil.isTrue(module.getId() == null, "待更新记录不存在!");
        Module temp = moduleMapper.selectByPrimaryKey(module.getId());
        AssertUtil.isTrue(temp == null, "待更新记录不存在!");

        //grade   非空0|1|2
        Integer grade = module.getGrade();
        AssertUtil.isTrue(grade == null || !(grade == 0 || grade == 1 || grade == 2), "菜单层级只能为1/2/3！");

        //moduleName  非空且同父菜单id下模块名称唯一（不包含当前记录本身）
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "模块名称不能为空!");
        temp = moduleMapper.selectByGradeAndModuleNameAndParentId(grade, module.getModuleName(), module.getParentId());
        AssertUtil.isTrue(temp != null && !module.getId().equals(temp.getId()), "该层级下模块名称已存在!");

        //url     二级菜单（grade=1），非空且同一层级下不可重复（不包含当前记录本身）
        if (module.getGrade() == 1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "层级为二级菜单时url不可为空!");
            temp = moduleMapper.selectByGradeAndUrl(grade, module.getUrl());
            AssertUtil.isTrue(temp != null && !module.getUrl().equals(temp.getUrl()), "URL已存在!");
        }

        //optValue    非空且不可重复（不包含当前记录本身）
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不可为空！");
        temp = moduleMapper.selectByOptValue(module.getOptValue());
        AssertUtil.isTrue(temp != null && !module.getOptValue().equals(temp.getOptValue()), "权限码已存在!");

        //2、设置默认参数
        module.setUpdateDate(new Date());

        //3、执行更新操作并判断执行结果
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module) != 1, "更新资源失败!");
    }

    /**
     * 删除资源
     *     1、参数校验
     *          id 非空且数据存在
     *     2、如果当前资源下存在子记录则不可删除
     *     3、判断权限表中是否存在关联记录，如果存在则删除
     *     4、执行删除操作并判断执行结果(将is_valid字段更改为0)
     * @param id
     */
    @Transactional
    public void deleteModule(Integer id) {
        //1、参数校验
        AssertUtil.isTrue(null == id, "待删除记录不存在!");
        Module temp = moduleMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null == temp, "待删除记录不存在!");

        //2、如果当前资源下存在子记录则不可删除
        AssertUtil.isTrue(0 != moduleMapper.countByParentId(id), "当前菜单下存在子菜单，不可删除!");

        //3、判断权限表中是否存在关联记录，如果存在则删除
        Integer permissionCount = permissionMapper.countPermissionByModuleId(id);
        if (permissionCount > 0){
            AssertUtil.isTrue(permissionMapper.deletePermissionByModuleId(id) != permissionCount, "删除资源失败!");
        }

        //4、设置参数
        temp.setUpdateDate(new Date());
        temp.setIsValid((byte) 0);

        //5执行删除操作并判断执行结果(将is_valid字段更改为0)
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(temp) != 1, "删除资源失败!");
    }
}

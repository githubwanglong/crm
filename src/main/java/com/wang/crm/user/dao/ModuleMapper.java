package com.wang.crm.user.dao;

import com.wang.crm.base.BaseMapper;
import com.wang.crm.user.domain.Module;
import com.wang.crm.user.model.TreeModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module, Integer> {

    //查询所有有效资源列表，供前台zTree树形结构使用
    List<TreeModel> queryAllModules();

    //查询所有有效资源列表，供前台表格使用
    List<Module> queryModuleList();

    //根据层级、模块名称、父级id查询资源对象
    Module selectByGradeAndModuleNameAndParentId(@Param("grade") Integer grade, @Param("moduleName") String moduleName ,@Param("parentId") Integer parentId);

    //根据层级和url查询资源对象
    Module selectByGradeAndUrl(@Param("grade") Integer grade, @Param("url") String url);


    //根据权限码查询资源对象
    Module selectByOptValue(String optValue);

    //根据父id查询资源
    Integer countByParentId(Integer parentId);

}
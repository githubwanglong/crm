package com.wang.crm.user.web.controller;

import com.wang.crm.annotation.RequiredPermission;
import com.wang.crm.base.BaseController;
import com.wang.crm.base.ResultInfo;
import com.wang.crm.user.domain.Module;
import com.wang.crm.user.model.TreeModel;
import com.wang.crm.user.service.ModuleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {

    @Resource
    private ModuleService moduleService;


    /**
     * 查询所有资源列表，供前台zTree使用
     * @return
     */
    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeModel> queryAllModules(Integer roleId){
        return moduleService.queryAllModules(roleId);
    }


    /**
     * 打开角色授权页面
     * @return
     */
    @RequestMapping("toGrantPage")
    public String toGrantPage(HttpServletRequest request, Integer roleId){
        //将需要授权的角色id放入到作用域中
        request.setAttribute("roleId", roleId);
        return "role/grant";
    }


    /**
     * 查询所有资源数据，供前台表格使用
     * @return
     */
    @RequiredPermission(code = "603002")
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryModuleList(){
        return moduleService.queryModuleList();
    }


    /**
     * 进入菜单管理界面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        //将需要授权的角色id放入到作用域中
        return "module/index";
    }


    /**
     * 添加资源
     * @param module
     * @return
     */
    @RequiredPermission(code = "603001")
    @PostMapping("add")
    @ResponseBody()
    public ResultInfo addModule(Module module){
        moduleService.addModule(module);
        return success("添加资源成功!");
    }


    /**
     * 进入添加资源的视图
     * @param grade 层级
     * @param parentId  父菜单ID
     * @return
     */
    @RequestMapping("toAddModulePage")
    public String toAddModulePage(HttpServletRequest request, Integer grade, Integer parentId){
        //将层级和父级菜单目录放入请求作用域中
        request.setAttribute("grade", grade);
        request.setAttribute("parentId", parentId);
        return "module/add";
    }



    /**
     * 添加资源
     * @param module
     * @return
     */
    @PostMapping("update")
    @ResponseBody()
    public ResultInfo updateModule(Module module){
        moduleService.updateModule(module);
        return success("更新资源成功!");
    }

    /**
     * 进入更新资源的视图
     * @param request
     * @param id
     * @return
     */
    @RequiredPermission(code = "603003")
    @RequestMapping("toUpdateModulePage")
    public String toUpdateModulePage(HttpServletRequest request, Integer id){
        Module module = moduleService.selectByPrimaryKey(id);
        //将需要更新的模块对象放入请求作用域中
        request.setAttribute("module", module);
        return "module/update";
    }

    /**
     * 删除资源
     * @param id
     * @return
     */
    @RequiredPermission(code = "603004")
    @PostMapping("delete")
    @ResponseBody()
    public ResultInfo deleteModule(Integer id){
        moduleService.deleteModule(id);
        return success("删除资源成功!");
    }
}

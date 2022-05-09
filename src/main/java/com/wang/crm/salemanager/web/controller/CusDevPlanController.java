package com.wang.crm.salemanager.web.controller;


import com.wang.crm.annotation.RequiredPermission;
import com.wang.crm.base.BaseController;
import com.wang.crm.base.ResultInfo;
import com.wang.crm.salemanager.domain.CusDevPlan;
import com.wang.crm.salemanager.domain.SaleChance;
import com.wang.crm.salemanager.query.CusDevPlanQuery;
import com.wang.crm.salemanager.service.CusDevPlanService;
import com.wang.crm.salemanager.service.SaleChanceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping(value = "cus_dev_plan")
public class CusDevPlanController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private CusDevPlanService cusDevPlanService;
    /**
     * 进入客户开发计划视图
     * @return
     */
    @RequiredPermission(code = "1020")
    @RequestMapping(value = "index")
    public String index(){
        return "cusDevPlan/cus_dev_plan";
    }

    /**
     * 打开客户开发计划项页面
     * @return
     */
    @RequestMapping(value = "toCusDevPlanPage")
    public String toCusDevPlanPage(Integer id, HttpServletRequest request){

        //通过id查询营销机会，返回营销机会对象。
        SaleChance saleChance = saleChanceService.selectById(id);

        //将营销机会对象放入请求作用域中。
        request.setAttribute("saleChance", saleChance);
        return "cusDevPlan/cus_dev_plan_data";
    }


    /**
     * 客户开发计划项数据查询（多条件分页查询）
     * @param cusDevPlanQuery
     * @return
     */
    @RequiredPermission(code = "102001")
    @GetMapping(value = "list")
    @ResponseBody
    public Map<String, Object> queryCusDevPlanByParam(CusDevPlanQuery cusDevPlanQuery){
        return cusDevPlanService.queryCusDevPlanByParam(cusDevPlanQuery);
    }


    /**
     * 添加计划项
     * @param cusDevPlan
     * @return
     */
    @RequiredPermission(code = "102003")
    @PostMapping(value = "add")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success("添加成功!");
    }


    /**
     * 进入添加或修改计划项的视图
     * @param request
     * @param sId
     * @return
     */
    @RequiredPermission(code = "102002")
    @GetMapping(value = "toAddOrUpdateCusDevPlanPage")
    public String toAddOrUpdateCusDevPlanPage(HttpServletRequest request, Integer sId, Integer id){
        //将营销机会id设置到请求作用域中，在计划项页面中使用
        request.setAttribute("sId", sId);

        //根据id获取计划项数据
        CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
        //将计划项数据放入请求作用域中。
        request.setAttribute("cusDevPlan", cusDevPlan);
        return "cusDevPlan/add_update";
    }


    /**
     * 更新客户开发计划项
     * @param cusDevPlan
     * @return
     */
    @RequiredPermission(code = "102004")
    @PostMapping(value = "update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("更新成功!");
    }


    /**
     * 删除计划项
     * @param id
     * @return
     */
    @RequiredPermission(code = "102005")
    @PostMapping(value = "delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer id){
        //执行删除操作返回结果
        cusDevPlanService.deleteCusDevPlan(id);
        return success("删除成功");

    }
}

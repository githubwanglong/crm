package com.wang.crm.salemanager.web.controller;

import com.wang.crm.annotation.RequiredPermission;
import com.wang.crm.base.BaseController;
import com.wang.crm.base.ResultInfo;
import com.wang.crm.salemanager.domain.SaleChance;
import com.wang.crm.salemanager.enums.StateStatus;
import com.wang.crm.salemanager.query.SaleChanceQuery;
import com.wang.crm.salemanager.service.SaleChanceService;
import com.wang.crm.user.model.UserModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
* 营销机会控制器
*/
@Controller
@RequestMapping(value = "sale_chance")
public class SaleChanceController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    /**
     * 营销机会数据查询（多条件分页查询）
     *  如果flag的值为空则表示是查询营销机会数据
     *  如果flag的值不为空且值为1则表示是客户开发计划
     * @param saleChanceQuery
     * @return
     */
    @RequiredPermission(code = "101001")
    @GetMapping(value = "list")
    @ResponseBody
    public Map<String, Object> querySaleChanceByParam(SaleChanceQuery saleChanceQuery, Integer flag, HttpSession session){
        //判断flag的值
        if (flag != null && flag == 1){
            //查询客户开发计划
            //设置分配状态
            saleChanceQuery.setState(StateStatus.STATED.getType());
            //设置分配人，当前登录用户
            UserModel user = (UserModel) session.getAttribute("user");
            saleChanceQuery.setAssignMan(user.getId());
        }
        return saleChanceService.querySaleChanceByParam(saleChanceQuery);
    }


    /**
     * 进入营销机会管理视图
     * @return
     */
    @RequiredPermission(code = "1010")
    @GetMapping(value = "index")
    public String index(){
        return "saleChance/sale_chance";
    }


    /**
     *添加营销机会
     * @param saleChance
     * @return
     */
    @RequiredPermission(code = "101002")
    @PostMapping(value = "add")
    @ResponseBody
    public ResultInfo addSaleChance(HttpSession session, SaleChance saleChance){
        UserModel user = (UserModel) session.getAttribute("user");
        //设置创建人
        saleChance.setCreateMan(user.getTrueName());
        //调用service层
        saleChanceService.addSaleChance(saleChance);
        //返回处理结果
        return success();
    }


    /**
     *更新营销机会
     * @param saleChance
     * @return
     */
    @RequiredPermission(code = "101004")
    @PostMapping(value = "update")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance){
        saleChanceService.updateSaleChance(saleChance);
        //返回处理结果
        return success();
    }


    /**
     * 进入添加、修改营销机会视图
     * @return
     */
    @RequestMapping("toSaleChancePage")
    public String toSaleChancePage(HttpServletRequest request, Integer saleChanceId){

        //判断营销机会id是否为空
        if (saleChanceId != null){
            //通过营销机会id查询营销机会
            SaleChance saleChance = saleChanceService.selectById(saleChanceId);
            request.setAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }


    /**
     * 删除营销机会
     * @param ids id数组
     * @return
     */
    @RequiredPermission(code = "101003")
    @PostMapping(value = "delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        //调用业务方法
        saleChanceService.deleteSaleChance(ids);

        //返回执行结果
        return success("营销机会数据删除成功!");
    }


    /**
     * 更新营销机会的开发状态
     * @param id
     * @param devResult
     * @return
     */
    @RequiredPermission(code = "102006")
    @PostMapping(value = "updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id, Integer devResult){
        saleChanceService.updateDevResult(id, devResult);
        return success("开发状态更新成功");
    }
}

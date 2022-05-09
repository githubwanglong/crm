package com.wang.crm.serveManager.controller;

import com.wang.crm.annotation.RequiredPermission;
import com.wang.crm.base.BaseController;
import com.wang.crm.base.ResultInfo;
import com.wang.crm.serveManager.domain.CustomerServe;
import com.wang.crm.serveManager.query.CustomerServeQuery;
import com.wang.crm.serveManager.service.CustomerServeService;
import com.wang.crm.user.domain.User;
import com.wang.crm.user.model.UserModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("customer_serve")
public class CustomerServeController extends BaseController {

    @Resource
    private CustomerServeService customerServeService;


    /**
     * 多条件分页查询服务数据列表
     * @param customerServeQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryCustomerServeByParams(HttpSession session, CustomerServeQuery customerServeQuery, Integer flag){
        //判断是否有分配人
        if (flag != null && flag == 1){
            //如果有分配人，只查询出分配给当前登录用户的服务
            UserModel userModel = (UserModel) session.getAttribute("user");
            customerServeQuery.setAssigner(String.valueOf(userModel.getId()));
        }
        return customerServeService.queryCustomerServeByParams(customerServeQuery);
    }


    /**
     * 进入对应的服务页面
     * @param serveType
     * @return
     */
    @RequestMapping("/index/{serveType}")
    public String index(@PathVariable("serveType")Integer serveType){
        if (serveType == 1){//服务创建
            return "customerServe/customer_serve";
        }else if (serveType == 2){//服务分配
            return "customerServe/customer_serve_assign";
        }else if (serveType == 3){//服务处理
            return "customerServe/customer_serve_proce";
        }else if (serveType == 4){//服务反馈
            return "customerServe/customer_serve_feed_back";
        }else if (serveType == 5){//服务归档
            return "customerServe/customer_serve_archive";
        }else{
            return "";
        }
    }



    /**
     * 进入添加服务的视图
     * @return
     */
    @RequestMapping("toAddCustomerServePage")
    public String toAddCustomerServePage(){
        return "customerServe/customer_serve_add";
    }


    /**
     * 添加服务
     * @param session
     * @param customerServe
     * @return
     */
    @RequiredPermission(code = "301001")
    @ResponseBody
    @PostMapping("add")
    public ResultInfo addCustomerServe(HttpSession session, CustomerServe customerServe){
        //设置服务创建人
        UserModel user = (UserModel) session.getAttribute("user");
        customerServe.setCreatePeople(user.getTrueName());
        customerServeService.addCustomerServe(customerServe);
        return success("添加服务成功!");
    }


    /**
     * 更新服务
     *      1、服务分配
     *      2、服务处理
     *      3、服务反馈
     * @param customerServe
     * @return
     */
    @RequiredPermission(code = "304001")
    @ResponseBody
    @PostMapping("update")
    public ResultInfo updateCustomerServe(CustomerServe customerServe){
        customerServeService.updateCustomerServe(customerServe);
        return success("更新服务成功!");
    }


    /**
     * 进入服务分配的视图
     * @return
     */
    @RequestMapping("openCustomerServeAssignPage")
    public String openCustomerServeAssignPage(HttpServletRequest request, Integer id){
        CustomerServe customerServe = customerServeService.selectByPrimaryKey(id);
        request.setAttribute("customerServe", customerServe);
        return "customerServe/customer_serve_assign_add";
    }



    /**
     * 进入服务处理的视图
     * @return
     */
    @RequestMapping("openCustomerServeProcePage")
    public String openCustomerServeProcePage(HttpServletRequest request, Integer id){
        CustomerServe customerServe = customerServeService.selectByPrimaryKey(id);
        request.setAttribute("customerServe", customerServe);
        return "customerServe/customer_serve_proce_add";
    }


    /**
     * 进入服务反馈的视图
     * @return
     */
    @RequestMapping("openCustomerServeBackPage")
    public String openCustomerServeBackPage(HttpServletRequest request, Integer id){
        CustomerServe customerServe = customerServeService.selectByPrimaryKey(id);
        request.setAttribute("customerServe", customerServe);
        return "customerServe/customer_serve_feed_back_add";
    }
}

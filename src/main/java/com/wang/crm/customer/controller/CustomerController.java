package com.wang.crm.customer.controller;

import com.wang.crm.annotation.RequiredPermission;
import com.wang.crm.base.BaseController;
import com.wang.crm.base.ResultInfo;
import com.wang.crm.customer.domain.Customer;
import com.wang.crm.customer.query.CustomerQuery;
import com.wang.crm.customer.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer")
public class CustomerController extends BaseController {

    @Resource
    private CustomerService customerService;


    /**
     * 多条件分页查询客户
     * @param customerQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryCustomerByParams(CustomerQuery customerQuery){
        return customerService.queryCustomerByParams(customerQuery);
    }


    /**
     * 进入客户信息管理页面
     * @return
     */
    @RequiredPermission(code = "2010")
    @GetMapping("index")
    public String index(){
        return "customer/customer";
    }


    /**
     * 添加客户
     * @param customer
     * @return
     */
    @RequiredPermission(code = "201001")
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addCustomer(Customer customer){
        customerService.addCustomer(customer);
        return success("添加客户信息成功!");
    }


    /**
     * 进入打开添加/更新客户页面
     * @return
     */
    @GetMapping("openAddOrupdateCustomerDialog")
    public String openAddOrupdateCustomerDialog(HttpServletRequest request, Integer customerId){

        //customerId为空是添加操作
        //customerId不为空是更新操作
        if (customerId != null){
            Customer customer = customerService.selectByPrimaryKey(customerId);
            request.setAttribute("customer", customer);
        }
        return "customer/add_update";
    }


    /**
     * 更新客户
     * @param customer
     * @return
     */
    @RequiredPermission(code = "201002")
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateCustomer(Customer customer){
        customerService.updateCustomer(customer);
        return success("更新客户信息成功!");
    }


    /**
     * 删除客户（逻辑删除，将is_valid字段更改为0）
     * @param
     * @return
     */
    @RequiredPermission(code = "201003")
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteCustomer(Integer id){
        customerService.deleteCustomer(id);
        return success("删除客户信息成功!");
    }


    /**
     * 进入客户订单记录页面
     * @return
     */
    @RequiredPermission(code = "102004")
    @GetMapping("openCustomerOrderPage")
    public String openCustomerOrderPage(HttpServletRequest request, Integer customerId){

        //通过客户id查询客户放入请求作用域中
        Customer customer = customerService.selectByPrimaryKey(customerId);
        request.setAttribute("customer", customer);

        return "customer/customer_order";
    }

    /**
     * 多条件查询客户贡献数据
     * @param customerQuery
     * @return
     */
    @RequiredPermission(code = "401001")
    @RequestMapping("queryCustomerContributionByParamsList")
    @ResponseBody
    public Map<String, Object> queryCustomerContributionByParams(CustomerQuery customerQuery){
        return customerService.queryCustomerContributionByParams(customerQuery);
    }


    /**
     * 查询客户构成（折线图）
     * @return
     */
    @RequiredPermission(code = "402001")
    @RequestMapping("countCustomerMake")
    @ResponseBody
    public Map<String, Object> queryCustomerMake(){
        return customerService.queryCustomerMake();
    }


    /**
     * 查询客户构成（饼状图）
     * @return
     */
    @RequiredPermission(code = "402001")
    @RequestMapping("countCustomerMake02")
    @ResponseBody
    public Map<String, Object> queryCustomerMake02(){
        return customerService.queryCustomerMake02();
    }
}

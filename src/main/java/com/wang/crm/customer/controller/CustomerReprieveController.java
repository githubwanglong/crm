package com.wang.crm.customer.controller;

import com.wang.crm.annotation.RequiredPermission;
import com.wang.crm.base.BaseController;
import com.wang.crm.base.ResultInfo;
import com.wang.crm.customer.domain.CustomerReprieve;
import com.wang.crm.customer.query.CustomerReprieveQuery;
import com.wang.crm.customer.service.CustomerReprieveService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("customer_rep")
@Controller
public class CustomerReprieveController extends BaseController {

    @Resource
    private CustomerReprieveService customerReprieveService;

    /**
     * 分页查询，根据流失客户ID查询
     * @param customerReprieveQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryLossCustomerRepByLossId(CustomerReprieveQuery customerReprieveQuery){
        return customerReprieveService.queryLossCustomerRepByLossId(customerReprieveQuery);
    }


    /**
     * 添加暂缓记录
     * @param customerReprieve
     * @return
     */
    @RequiredPermission(code = "202001")
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addReprieve(CustomerReprieve customerReprieve){
        customerReprieveService.addCustomerReprieve(customerReprieve);
        return success("添加暂缓数据成功!");
    }


    /**
     * 更新暂缓记录
     * @param customerReprieve
     * @return
     */
    @RequiredPermission(code = "202002")
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateReprieve(CustomerReprieve customerReprieve){
        customerReprieveService.updateCustomerReprieve(customerReprieve);
        return success("更新暂缓数据成功!");
    }


    /**
     * 删除暂缓记录
     * @param reprieveId
     * @return
     */
    @RequiredPermission(code = "202003")
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteReprieve(Integer reprieveId){
        customerReprieveService.deleteCustomerReprieve(reprieveId);
        return success("删除暂缓数据成功!");
    }

    /**
     * 打开添加/更新暂缓的页面
     * @return
     */
    @RequestMapping("toAddOrUpdateReprievePage")
    public String toAddOrUpdateReprievePage(HttpServletRequest request, Integer lossId, Integer id){
        //判断id是否为空，id为空是添加操作，id不为空则是更新操作
        if (id != null){
            CustomerReprieve customerRep = customerReprieveService.selectByPrimaryKey(id);
            //将查询到的数据放入作用域中
            request.setAttribute("customerRep", customerRep);
        }
        //将流失客户id存入请求作用域中供前台获取
        request.setAttribute("lossId", lossId);
        return "customerLoss/customer_rep_add_update";
    }
}

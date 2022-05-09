package com.wang.crm.serveManager.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.crm.base.BaseService;
import com.wang.crm.customer.dao.CustomerMapper;
import com.wang.crm.customer.domain.Customer;
import com.wang.crm.salemanager.enums.CustomerServeStatus;
import com.wang.crm.serveManager.dao.CustomerServeMapper;
import com.wang.crm.serveManager.domain.CustomerServe;
import com.wang.crm.serveManager.query.CustomerServeQuery;
import com.wang.crm.user.dao.UserMapper;
import com.wang.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerServeService extends BaseService<CustomerServe, Integer> {

    @Resource
    private CustomerServeMapper customerServeMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private UserMapper userMapper;


    /**
     * 多条件分页查询服务数据列表
     * @param customerServeQuery
     * @return
     */
    public Map<String, Object> queryCustomerServeByParams(CustomerServeQuery customerServeQuery) {
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerServeQuery.getPage(), customerServeQuery.getLimit());

        //得到分页对象
        PageInfo<CustomerServe> pageInfo = new PageInfo<>(customerServeMapper.selectByParams(customerServeQuery));

        //放入集合
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());

        //返回集合
        return map;
    }

    /**
     * 添加服务
     *      1、参数校验
     *          客户名称customer    非空且客户存在
     *          服务类型serveType    非空
     *          服务请求内容serviceRequest    非空
     *
     *      2、设置参数默认值
     *          服务状态state    fw_001
     *          是否有效isValid    1
     *          创建时间    系统当前时间
     *          创建人(controller层中已设置)
     *
     *      3、执行添加操作并判断执行结果
     * @param customerServe
     */
    @Transactional
    public void addCustomerServe(CustomerServe customerServe) {
        //1、参数判断
        //客户名称非空校验
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getCustomer()), "客户不存在!");
        //客户是否存在校验
        Customer temp = customerMapper.queryCustomerByName(customerServe.getCustomer());
        AssertUtil.isTrue(null == temp, "客户不存在!");
        //服务类型非空校验
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServeType()), "服务类型不能为空!");
        //服务请求内容非空校验
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceRequest()), "服务请求内容不能为空!");

        //2、设置默认参数
        customerServe.setIsValid(1);
        customerServe.setCreateDate(new Date());
        customerServe.setState(CustomerServeStatus.CREATED.getState());

        //3、执行添加操作并判断执行结果
        AssertUtil.isTrue(customerServeMapper.insertSelective(customerServe) != 1, "添加服务失败!");
    }


    /**
     * 服务分配|服务处理|服务反馈
     *      1、参数校验和设置默认值
     *          服务ID    非空且数据存在
     *          客户服务状态  如果服务状态为-服务分配状态：fw_002
     *                          分配人     非空且分配人信息存在
     *                          分配时间    当前系统时间
     *                          更新时间    当前系统时间
     *
     *                      如果服务状态为-服务处理状态：fw_003
     *                          服务处理内容  非空
     *                          服务处理时间  系统当前时间
     *                          更新时间    当前系统时间
     *
     *                      如果服务状态为-服务反馈状态：：fw_004
     *                          反馈内容    非空
     *                          服务满意度   非空
     *                          更新时间    系统当前时间
     *                          服务状态    服务归档状态fw_005
     *
     *      2、执行更新操作并判断执行结果
     */
    @Transactional
    public void updateCustomerServe(CustomerServe customerServe){
        //客户服务ID非空且数据存在
        AssertUtil.isTrue(customerServe.getId() == null, "服务数据不存在!");
        CustomerServe temp = customerServeMapper.selectByPrimaryKey(customerServe.getId());
        AssertUtil.isTrue(temp == null, "服务数据不存在!");

        //判断服务状态
        if (CustomerServeStatus.ASSIGNED.getState().equals(customerServe.getState())){//分配状态
            //参数校验 分配人     非空且分配人信息存在
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getAssigner()), "分配人不能为空");
            AssertUtil.isTrue(userMapper.selectByPrimaryKey(Integer.valueOf(customerServe.getAssigner())) == null, "待分配人不存在!");
            //设置参数
            customerServe.setAssignTime(new Date());

        }else if(CustomerServeStatus.PROCED.getState().equals(customerServe.getState())){//处理状态
            //处理人 非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProcePeople()), "服务处理人不存在!");
            //服务处理内容  非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProce()), "服务处理内容不能为空!");
            //服务处理时间  系统当前时间
            customerServe.setServiceProceTime(new Date());

        }else if(CustomerServeStatus.FEED_BACK.getState().equals(customerServe.getState())){//反馈状态
            //反馈内容    非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProceResult()), "服务反馈内容不能为空!");
            //服务满意度   非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getMyd()), "请选择服务满意度!");
            //服务状态    服务归档状态fw_005
            customerServe.setState(CustomerServeStatus.ARCHIVED.getState());
        }
        //更新时间    系统当前时间
        customerServe.setUpdateDate(new Date());
        //执行更新操作并判断执行结果
        AssertUtil.isTrue(customerServeMapper.updateByPrimaryKeySelective(customerServe) != 1, "更新服务失败!");
    }
}

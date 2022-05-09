package com.wang.crm.customer.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.crm.base.BaseService;
import com.wang.crm.customer.dao.CustomerLossMapper;
import com.wang.crm.customer.dao.CustomerReprieveMapper;
import com.wang.crm.customer.domain.Customer;
import com.wang.crm.customer.domain.CustomerReprieve;
import com.wang.crm.customer.query.CustomerReprieveQuery;
import com.wang.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerReprieveService extends BaseService<CustomerReprieve, Integer> {

    @Resource
    private CustomerReprieveMapper customerReprieveMapper;

    @Resource
    private CustomerLossMapper customerLossMapper;


    /**
     * 根据loosId查询暂缓详情(分页查询)
     * @param customerReprieveQuery
     * @return
     */
    public Map<String, Object> queryLossCustomerRepByLossId(CustomerReprieveQuery customerReprieveQuery) {
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerReprieveQuery.getPage(), customerReprieveQuery.getLimit());
        //得到对应分页对象
        PageInfo<Customer> pageInfo = new PageInfo(customerReprieveMapper.selectByParams(customerReprieveQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //需要分页的数据
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加暂缓数据
     *      1、参数判断
     *          流失客户ID lossId 非空且流失客户数据存在
     *          暂缓措施内容 measure  非空
     *
     *      2、设置参数的默认值
     *          createDate 系统当前时间
     *          isValid     1
     *
     *      3、执行添加操作并判断执行结果
     *
     * @param customerReprieve
     */
    @Transactional
    public void addCustomerReprieve(CustomerReprieve customerReprieve) {
        //1、参数校验
        AssertUtil.isTrue(customerReprieve.getLossId() == null, "流失客户不存在!");
        AssertUtil.isTrue(customerLossMapper.selectByPrimaryKey(customerReprieve.getLossId()) == null, "流失客户不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(customerReprieve.getMeasure()), "暂缓措施内容不能为空!");

        //2、设置参数的默认值
        customerReprieve.setIsValid(1);
        customerReprieve.setCreateDate(new Date());

        //3、执行添加操作并判断执行结果
        AssertUtil.isTrue(customerReprieveMapper.insertSelective(customerReprieve) != 1, "添加暂缓数据失败!");
    }



    /**
     * 更新暂缓数据
     *      1、参数判断
     *          id  非空且数据存在
     *          流失客户ID lossId 非空且流失客户数据存在
     *          暂缓措施内容 measure  非空
     *
     *      2、设置参数的默认值
     *          updateDate 系统当前时间
     *
     *      3、执行添加操作并判断执行结果
     *
     *
     * @param customerReprieve
     */
    @Transactional
    public void updateCustomerReprieve(CustomerReprieve customerReprieve) {
        //1、参数校验
        AssertUtil.isTrue(customerReprieve.getId() == null, "待更新数据不存在!");
        AssertUtil.isTrue(customerReprieveMapper.selectByPrimaryKey(customerReprieve.getId()) == null, "待更新数据不存在!");
        AssertUtil.isTrue(customerReprieve.getLossId() == null, "流失客户不存在!");
        AssertUtil.isTrue(customerLossMapper.selectByPrimaryKey(customerReprieve.getLossId()) == null, "流失客户不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(customerReprieve.getMeasure()), "暂缓措施内容不能为空!");

        //2、设置参数默认值
        customerReprieve.setUpdateDate(new Date());

        //3、执行更新操作并判断执行结果
        AssertUtil.isTrue(customerReprieveMapper.updateByPrimaryKeySelective(customerReprieve) != 1, "更新暂缓数据失败!");
    }


    /**
     * 删除暂缓数据（逻辑删除，将is_valid字段更改为0）
     *      1、参数判断
     *          id  非空且数据存在
     *
     *      2、更改参数
     *          isValid = 0
     *          updateDate  系统当前时间
     *
     *      3、执行删除操作并判断执行结果
     *
     * @param reprieveId
     */
    public void deleteCustomerReprieve(Integer reprieveId) {
        //1、参数校验
        AssertUtil.isTrue(reprieveId == null, "待删除记录不存在!");
        CustomerReprieve temp = customerReprieveMapper.selectByPrimaryKey(reprieveId);
        AssertUtil.isTrue(temp == null, "待删除记录不存在!");

        //2、设置参数
        temp.setIsValid(0);
        temp.setUpdateDate(new Date());

        //3.执行删除操作并判断执行结果
        AssertUtil.isTrue(customerReprieveMapper.updateByPrimaryKeySelective(temp) != 1, "删除暂缓数据失败!");
    }
}

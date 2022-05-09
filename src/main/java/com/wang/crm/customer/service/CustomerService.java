package com.wang.crm.customer.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.crm.base.BaseService;
import com.wang.crm.customer.dao.CustomerLossMapper;
import com.wang.crm.customer.dao.CustomerMapper;
import com.wang.crm.customer.dao.CustomerOrderMapper;
import com.wang.crm.customer.domain.Customer;
import com.wang.crm.customer.domain.CustomerLoss;
import com.wang.crm.customer.domain.CustomerOrder;
import com.wang.crm.customer.query.CustomerQuery;
import com.wang.crm.utils.AssertUtil;
import com.wang.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class CustomerService extends BaseService<Customer, Integer> {

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private CustomerOrderMapper customerOrderMapper;

    @Resource
    private CustomerLossMapper customerLossMapper;

    /**
     * 多条件分页查询客户。(返回的数据格式必须满足LayUi中数据表格要求的格式)
     * @param customerQuery
     * @return
     */
    public Map<String, Object> queryCustomerByParams(CustomerQuery customerQuery){
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerQuery.getPage(), customerQuery.getLimit());
        //得到对应分页对象
        PageInfo<Customer> pageInfo = new PageInfo(customerMapper.selectByParams(customerQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //需要分页的数据
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加客户
     *      1、参数校验
     *          客户名称    非空且唯一
     *          法人  非空
     *          手机号码    非空且格式正确
     *
     *      2、设置默认值
     *          是否有效    1
     *          创建时间    系统当前时间
     *          客户编号    系统生成，唯一值（uuid | 时间戳 | 年月日时分秒 | 雪花算法）
     *                     KH+时间戳
     *          流失状态    0
     *      3、执行添加操作并判断执行结果
     * @param customer
     */
    @Transactional
    public void addCustomer(Customer customer){
        //参数校验
        checkCustomerParams(customer);
        AssertUtil.isTrue(null != customerMapper.queryCustomerByName(customer.getName()), "客户名称已存在!");

        //设置默认值
        customer.setIsValid(1);
        customer.setCreateDate(new Date());
        String khno = "KH" + System.currentTimeMillis();
        customer.setKhno(khno);
        customer.setState(0);

        //执行添加操作并判断执行结果
        AssertUtil.isTrue(customerMapper.insertSelective(customer) != 1, "添加客户失败!");
    }


    /**
     * 添加客户的参数校验
     * @param customer
     */
    private void checkCustomerParams(Customer customer) {
        AssertUtil.isTrue(StringUtils.isBlank(customer.getName()), "客户名称不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(customer.getFr()), "法人不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(customer.getPhone()), "手机号码不能为空!");
        AssertUtil.isTrue(!PhoneUtil.isMobile(customer.getPhone()), "手机号码格式不正确!");
    }



    /**
     * 更新客户
     *      1、参数校验
     *          id      非空且数据存在
     *          客户名称    非空且唯一（不包括自身）
     *          法人  非空
     *          手机号码    非空且格式正确
     *
     *      2、设置默认值
     *          最近更新时间    系统当前时间

     *      3、执行更新操作并判断执行结果
     * @param customer
     */
    @Transactional
    public void updateCustomer(Customer customer){
        //参数校验
        AssertUtil.isTrue(null == customer.getId(), "待更新记录不存在!");
        Customer temp = customerMapper.selectByPrimaryKey(customer.getId());
        AssertUtil.isTrue(temp == null, "待更新记录不存在!");

        checkCustomerParams(customer);

        //客户名称 是否唯一
        temp = customerMapper.queryCustomerByName(customer.getName());
        AssertUtil.isTrue(null != temp && !temp.getId().equals(customer.getId()), "客户名称已存在!");

        //设置默认值
        customer.setUpdateDate(new Date());

        //执行更新操作并判断执行结果
        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(customer) != 1, "更新客户失败!");
    }


    /**
     * 删除客户（逻辑删除，将is_valid字段更改为0）
     *      1、参数判断
     *          id  非空且数据存在
     *
     *      2、修改参数
     *          updateDate    系统当前时间
     *          isValid         0
     *
     *      3、执行修改操作并判断执行结果
     * @param id
     */
    @Transactional
    public void deleteCustomer(Integer id) {
        //判断id是否为空数据是否存在
        AssertUtil.isTrue(null == id, "待删除记录不存在!");
        Customer temp = customerMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null == temp, "待删除记录不存在!");

        //修改参数
        temp.setUpdateDate(new Date());
        temp.setIsValid(0);

        //执行修改操作并判断执行结果
        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(temp) != 1, "删除客户信息失败!");
    }


    /**
     * 更新客户的流失状态
     *    1、查询待流失的客户数据
     *    2、将流失客户的数据批量添加到客户流失表中
     *    3、批量更新客户的流失状态 0=正常客户  1=流失客户
     */
    @Transactional
    public void updateCustomerState(){
        //查询待流失的客户数据
        List<Customer> lossCustomersList = customerMapper.queryLossCustomers();

        //将流失客户的数据批量添加到客户流失表中
        //判断是否有待流失的客户
        if (lossCustomersList != null && lossCustomersList.size() > 0){
            //定义集合存放流失客户的id
            List<Integer> lossCusIdList = new ArrayList<>();
            //定义流失客户列表
            List<CustomerLoss> willLossList = new ArrayList<>();
            //遍历查询到的流失客户
            for (Customer customer : lossCustomersList){
                CustomerLoss customerLoss = new CustomerLoss();
                //创建时间
                customerLoss.setCreateDate(new Date());
                //客户经理
                customerLoss.setCusManager(customer.getCusManager());
                //客户名称
                customerLoss.setCusName(customer.getName());
                //客户编号
                customerLoss.setCusNo(customer.getKhno());
                //是否有效
                customerLoss.setIsValid(1);
                //流失状态 0=暂缓流失  1=确认流失
                customerLoss.setState(0);
                //最后下单时间
                //通过客户id查询客户最后的订单记录
                CustomerOrder customerOrder = customerOrderMapper.queryLossCustomerOrderByCustomerId(customer.getId());
                //判断客户订单是否存在，如果存在则设置最后下单时间
                if (customerOrder != null){
                    customerLoss.setLastOrderTime(customerOrder.getOrderDate());
                }
                //将流失客户对象放入集合中
                willLossList.add(customerLoss);

                //将流失客户的id放入集合
                lossCusIdList.add(customer.getId());
            }
            //批量添加流失客户记录
            AssertUtil.isTrue(customerLossMapper.insertBatch(willLossList) != willLossList.size(), "客户流失数据转移失败!");

            //批量更新客户的流失状态 0=正常客户  1=流失客户
            AssertUtil.isTrue(customerMapper.updateCustomerStateById(lossCusIdList) != lossCusIdList.size(), "客户流失数据转移失败!");
        }
    }


    /**
     * 多条件查询客户贡献数据
     * @param customerQuery
     * @return
     */
    public Map<String, Object> queryCustomerContributionByParams(CustomerQuery customerQuery){
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerQuery.getPage(), customerQuery.getLimit());
        //得到对应分页对象
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(customerMapper.queryCustomerContributionByParams(customerQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //需要分页的数据
        map.put("data", pageInfo.getList());
        return map;
    }


    /**
     * 查询客户构成数据(折线图数据处理)
     * @return
     */
    public Map<String, Object> queryCustomerMake(){
        Map<String, Object> map = new HashMap<>();
        //查询客户构成的数据列表
        List<Map<String, Object>> dataList = customerMapper.queryCustomerMake();
        //折线图x轴数据 需要数组
        List<String> xData = new ArrayList<>();
        //折线图y轴数据 需要数组
        List<Integer> yData = new ArrayList<>();

        //遍历查询到的数据设置需要的数据
        if (dataList != null && dataList.size() != 0){
            for (Map<String, Object> data : dataList){
                xData.add(data.get("level").toString());
                yData.add(Integer.valueOf(data.get("total").toString()));
            }
        }
        //将x轴和y轴数据放入集合中
        map.put("xData", xData);
        map.put("yData", yData);
        return map;
    }



    /**
     * 查询客户构成数据(饼状图数据处理)
     * @return
     */
    public Map<String, Object> queryCustomerMake02(){
        Map<String, Object> map = new HashMap<>();
        //查询客户构成的数据列表
        List<Map<String, Object>> dataList = customerMapper.queryCustomerMake();
        //饼状图数据 需要数组（数组中是字符串）
        List<String> data1 = new ArrayList<>();
        //饼状图数据 需要数组(数组中是对象)
        List<Map<String, Object>> data2 = new ArrayList<>();

        //遍历查询到的数据设置需要的数据
        if (dataList != null && dataList.size() != 0){
            //遍历结合
            dataList.forEach(m -> {
                //饼状图数据 需要数组（数组中是字符串）
                data1.add(m.get("level").toString());
                //饼状图数据 需要数组(数组中是对象)
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("name", m.get("level"));
                dataMap.put("value", m.get("total"));
                data2.add(dataMap);
            });
        }
        //将x轴和y轴数据放入集合中
        map.put("data1", data1);
        map.put("data2", data2);
        return map;
    }
}

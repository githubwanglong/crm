package com.wang.crm.customer.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.crm.base.BaseService;
import com.wang.crm.customer.dao.CustomerLossMapper;
import com.wang.crm.customer.domain.CustomerLoss;
import com.wang.crm.customer.domain.CustomerOrder;
import com.wang.crm.customer.query.CustomerLossQuery;
import com.wang.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerLossService extends BaseService<CustomerLoss, Integer> {

    @Resource
    private CustomerLossMapper customerLossMapper;


    /**
     * 多条件分页查询流失客户
     * @param customerLossQuery
     * @return
     */
    public Map<String, Object> queryLossCustomerByParams(CustomerLossQuery customerLossQuery) {
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerLossQuery.getPage(), customerLossQuery.getLimit());
        //得到对应分页对象
        PageInfo<CustomerOrder> pageInfo = new PageInfo(customerLossMapper.selectByParams(customerLossQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //需要分页的数据
        map.put("data", pageInfo.getList());
        return map;
    }


    /**
     * 更新客户的流失状态
     *      1、参数校验
     *          id 非空且数据存在
     *          流失原因    非空
     *
     *      2、设置参数
     *          流失状态    设置为1    0=暂缓流失  1=确认流失
     *          确认流失时间  系统当前时间
     *          流失原因
     *          更新时间    系统当前时间
     *
     *      3、执行更新操作并判断执行结果
     *
     * @param customerLoss
     */
    @Transactional
    public void updateCustomerLossStateById(CustomerLoss customerLoss) {
        //1、参数判断
        AssertUtil.isTrue(customerLoss.getId() == null, "待流失客户不存在!");
        CustomerLoss temp = customerLossMapper.selectByPrimaryKey(customerLoss.getId());
        AssertUtil.isTrue(null == temp, "待流失客户不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(customerLoss.getLossReason()), "流失原因不能为空!");

        //2、设置参数
        temp.setState(1);
        temp.setConfirmLossTime(new Date());
        temp.setLossReason(customerLoss.getLossReason());
        temp.setUpdateDate(new Date());

        //3、执行更新操作并判断执行结果
        AssertUtil.isTrue(customerLossMapper.updateByPrimaryKeySelective(temp) != 1, "确认流失失败!");
    }

    /**
     * 将流失客户恢复正常
     *      1、参数校验
     *             id 非空且数据存在
     *      2、设置参数
     *              流失状态    0
     *              更新时间    系统当前时间
     *      3、执行更新操作并判断执行结果
     * @param customerLoss
     */
    public void renewCustomerLossStateById(CustomerLoss customerLoss) {
        //1、参数判断
        AssertUtil.isTrue(customerLoss.getId() == null, "待恢复客户不存在!");
        CustomerLoss temp = customerLossMapper.selectByPrimaryKey(customerLoss.getId());
        AssertUtil.isTrue(null == temp, "待恢复客户不存在!");

        //2、设置参数
        temp.setState(0);
        temp.setUpdateDate(new Date());

        //3、执行更新操作并判断执行结果
        AssertUtil.isTrue(customerLossMapper.updateByPrimaryKeySelective(temp) != 1, "恢复流失客户失败!");
    }
}

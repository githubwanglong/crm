package com.wang.crm.salemanager.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.crm.base.BaseService;
import com.wang.crm.salemanager.dao.CusDevPlanMapper;
import com.wang.crm.salemanager.domain.CusDevPlan;
import com.wang.crm.salemanager.query.CusDevPlanQuery;
import com.wang.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CusDevPlanService extends BaseService<CusDevPlan, Integer> {

    @Resource
    private CusDevPlanMapper cusDevPlanMapper;

    @Resource
    private SaleChanceService saleChanceService;


    /**
     * 删除计划项
     *  1、判断参数是否为空
     *  2、执行删除操作（并不是真正的删除，只是将isValid字段更改为0）
     * @param id
     */
    @Transactional
    public void deleteCusDevPlan(Integer id) {
        //参数判断
        AssertUtil.isTrue(id == null, "待删除的计划项不存在!");
        //根据id查询计划项是否存在
        CusDevPlan cusDevPlan = cusDevPlanMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(cusDevPlan == null, "待删除的计划项不存在");
        //将isValid字段更改为0，更改最近修改时间
        cusDevPlan.setIsValid(0);
        cusDevPlan.setUpdateDate(new Date());
        //执行更新操作，判断执行结果
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan) != 1, "删除失败!");
    }

    /**
     * 多条件分页查询客户开发计划项。(返回的数据格式必须满足LayUi中数据表格要求的格式)
     * @param   cusDevPlanQuery
     * @return
     */
    public Map<String, Object> queryCusDevPlanByParam(CusDevPlanQuery cusDevPlanQuery){
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(cusDevPlanQuery.getPage(), cusDevPlanQuery.getLimit());
        //得到对应分页对象
        PageInfo<CusDevPlan> pageInfo = new PageInfo(cusDevPlanMapper.selectByParams(cusDevPlanQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //需要分页的数据
        map.put("data", pageInfo.getList());
        return map;
    }


    /**
     * 添加计划项
     *   1、参数校验
     *          营销机会id 非空且数据存在
     *          计划项内容   非空
     *          计划时间    非空
     *
     *   2、设置参数默认值
     *          是否有效  默认有效
     *          创建时间    系统当前时间
     *
     *   3、执行添加操作判断受影响行数
     *
     * @param cusDevPlan
     */
    @Transactional
    public void addCusDevPlan(CusDevPlan cusDevPlan){
        //参数校验
        checkCusDevPlanParams(cusDevPlan.getSaleChanceId(), cusDevPlan.getPlanItem(), cusDevPlan.getPlanDate());

        //设置参数默认值
        cusDevPlan.setIsValid(1);
        cusDevPlan.setCreateDate(new Date());

        //执行添加操作判断受影响行数
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan) != 1, "添加失败!");
    }


    /**
     * 更新计划项
     *   1、参数校验
     *          计划项id  非空且数据存在
     *          营销机会id 非空且数据存在
     *          计划项内容   非空
     *          计划时间    非空
     *
     *   2、设置参数默认值
     *          更新时间  当前系统时间
     *
     *   3、执行更新操作判断受影响行数
     *
     * @param cusDevPlan
     */
    @Transactional
    public void updateCusDevPlan(CusDevPlan cusDevPlan){
        //参数校验
        checkUpdateCusDevPlanParams(cusDevPlan.getId(), cusDevPlan.getSaleChanceId(), cusDevPlan.getPlanItem(), cusDevPlan.getPlanDate());

        //设置参数默认值
        cusDevPlan.setUpdateDate(new Date());

        //执行添加操作判断受影响行数
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan) != 1, "更新失败!");
    }


    /**
     * 校验更新计划项参数
     * @param id
     * @param saleChanceId
     * @param planItem
     * @param planDate
     */
    private void checkUpdateCusDevPlanParams(Integer id, Integer saleChanceId, String planItem, Date planDate) {
        AssertUtil.isTrue(id == null, "开发计划项记录不存在!");
        AssertUtil.isTrue(cusDevPlanMapper.selectByPrimaryKey(id) == null, "开发计划项记录不存在");
        checkCusDevPlanParams(saleChanceId, planItem, planDate);
    }


    /**
     * 校验添加开发计划项参数
     * @param saleChanceId
     * @param planItem
     * @param planDate
     */
    private void checkCusDevPlanParams(Integer saleChanceId, String planItem, Date planDate) {
        AssertUtil.isTrue(saleChanceId == null, "数据异常，请重试!");
        AssertUtil.isTrue(saleChanceService.selectById(saleChanceId) == null, "数据异常，请重试!");
        AssertUtil.isTrue(StringUtils.isBlank(planItem), "计划项内容不能为空!");
        AssertUtil.isTrue(planDate == null, "计划时间不能为空!");
    }
}

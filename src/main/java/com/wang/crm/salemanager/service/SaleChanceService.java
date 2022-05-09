package com.wang.crm.salemanager.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.crm.base.BaseService;
import com.wang.crm.salemanager.dao.SaleChanceMapper;
import com.wang.crm.salemanager.domain.SaleChance;
import com.wang.crm.salemanager.enums.DevResult;
import com.wang.crm.salemanager.enums.StateStatus;
import com.wang.crm.salemanager.query.SaleChanceQuery;
import com.wang.crm.utils.AssertUtil;
import com.wang.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 营销机会service
 */
@Service
public class SaleChanceService extends BaseService{

    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件分页查询营销机会。(返回的数据格式必须满足LayUi中数据表格要求的格式)
     * @param saleChanceQuery
     * @return
     */
    public Map<String, Object> querySaleChanceByParam(SaleChanceQuery saleChanceQuery){
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(saleChanceQuery.getPage(), saleChanceQuery.getLimit());
        //得到对应分页对象
        PageInfo<SaleChance> pageInfo = new PageInfo(saleChanceMapper.selectByParams(saleChanceQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //需要分页的数据
        map.put("data", pageInfo.getList());
        return map;
    }


    /**
     * 添加营销机会
     *
     * 1、参数校验
     *      customerName客户名称    非空
     *      linkMan联系人          非空
     *      linkPhone联系电话       非空，并且格式正确
     *
     * 2、设置相关参数默认值
     *      createMan创建人        当前登录用户真实姓名
     *      assignMan分配人        如果客户端传过来为空
     *                              那么分配状态state为未分配，0=未分配 1=已分配，
     *                              分配时间assignTime为null
     *                              开发状态devResult为未开发 0=未开发 1=开发中 2=开发成功 3=开发失败
     *                            如果客户端传过来不为空
     *                              那么那么分配状态state为已分配，
     *                              分配时间为系统当前时间
     *                              开发状态为开发中
     *      isValid是否有效        有效 0=无效 1=有效
     *      createDate创建时间      默认当前系统时间
     *      updateDate最近修改时间   默认为null
     *
     * 3、执行添加操作，判断受影响行数
     * @param saleChance
     */
    @Transactional
    public void addSaleChance(SaleChance saleChance){
        //校验参数
        checkSaleChanceParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        //设置相关属性默认值
        //isValid是否有效        默认有效 0=无效 1=有效
        saleChance.setIsValid(1);
        //createDate创建时间      默认当前系统时间
        saleChance.setCreateDate(new Date());
        //判断是否设置了分配人
        if (StringUtils.isBlank(saleChance.getAssignMan())){
            //分配人为空，则表示未设置分配人
            //分配状态state为未分配，0=未分配 1=已分配，
            saleChance.setState(StateStatus.UNSTATE.getType());
            //开发状态devResult为未开发 0=未开发 1=开发中 2=开发成功 3=开发失败
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        }else{
            //分配人不为空，则表示设置了分配人
            //分配状态state为已分配
            saleChance.setState(StateStatus.STATED.getType());
            //分配时间为系统当前时间
            saleChance.setAssignTime(new Date());
            //开发状态为开发中
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        }

        //调用数据库访问层
        Integer count = saleChanceMapper.insertSelective(saleChance);
        AssertUtil.isTrue(count != 1, "添加失败,等会在试试吧!");
    }


    /**
     * 更新营销机会
     *  1、参数校验
     *      id 非空，并且数据库中有对应的数据存在
     *      customerName客户名称  为空
     *      linkMan联系人  非空
     *      linkPhone   非空
     *
     *  2、设置相关参数默认值
     *      updateDate最近更新时间  设置为当前系统时间
     *      assignMan分配人
     *          原始数据未设置
     *              修改后未设置
     *                  不需要操作
     *              修改后已设置
     *                  assignTime分配时间 当前系统时间
     *                  分配状态    1已分配
     *                  开发状态    1开发中
     *          原始数据已设置
     *              修改后未设置
     *                  assignTime分配时间  设置为null
     *                  分配状态    0未分配
     *                  开发状态    0未开发
     *              修改后已设置
     *                  判断修改前后是否是同一个分配人
     *                      是   不需要操作
     *                      不是  则需要更新assignTime分配时间 当前系统时间
     *
     *  3、执行更新操作，判断受影响行数
     */
    @Transactional
    public void updateSaleChance(SaleChance saleChance){
        //参数校验
        AssertUtil.isTrue(null == saleChance.getId(), "待更新记录不存在");
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(null == temp, "待更新记录不存在");
        checkSaleChanceParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());

        //设置相关参数默认值
        saleChance.setUpdateDate(new Date());

        //assignMan指派人
        //判断原始数据是否有指派人
        if (StringUtils.isBlank(temp.getAssignMan())){//原始数据没有指派人
            //判断修改数据是否有指派人
            if (!StringUtils.isBlank(saleChance.getAssignMan())){//修改前为空，修改后有值
                //assignTime分配时间 当前系统时间
                saleChance.setAssignTime(new Date());
                //分配状态    1已分配
                saleChance.setState(StateStatus.STATED.getType());
                //开发状态    1开发中
                saleChance.setDevResult(DevResult.DEVING.getStatus());
            }
        }else{//原始数据有指派人
            //判断修改后是否有值
            if (StringUtils.isBlank(saleChance.getAssignMan())){//修改前有值，修改后没有值
                //assignTime分配时间  设置为null
                saleChance.setAssignTime(null);
                //分配状态    0未分配
                saleChance.setState(StateStatus.UNSTATE.getType());
                //开发状态    0未开发
                saleChance.setDevResult(DevResult.UNDEV.getStatus());
            }else {//修改前有值，修改后也有值
                //判断是否是同一个人
                if (temp.getAssignMan().equals(saleChance.getAssignMan())){
                    //是同一个人，指派时间不变
                    saleChance.setAssignTime(temp.getAssignTime());
                }else {
                    //不是同一个人，更新分配时间
                    saleChance.setAssignTime(new Date());
                }
            }

        }

        //执行更新操作，判断受影响行数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) != 1, "更新失败!");

    }


    /**
     * 添加营销机会参数校验
     * @param customerName 客户名成
     * @param linkMan   联系人
     * @param linkPhone 联系电话
     */
    private void checkSaleChanceParams(String customerName, String linkMan, String linkPhone) {

        /*
         *      customerName客户名称    非空
         *      linkMan联系人          非空
         *      linkPhone联系电话       非空，并且格式正确
         */
        //客户名称非空校验
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "客户名称不能为空");
        //联系人非空校验
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "联系人不能为空");
        //联系电话非空校验
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "联系电话不能为空");
        //联系电话格式校验
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone), "联系电话格式不正确");

    }


    /**
     * 根据id查询营销机会记录
     * @param id
     * @return
     */
    public SaleChance selectById(Integer id){
        return saleChanceMapper.selectByPrimaryKey(id);
    }


    /**
     * 批量删除操作
     *      并不是真正的删除，只是将is_valid字段更改为0
     * @param ids
     */
    @Transactional
    public void deleteSaleChance(Integer[] ids){
        //判断id数组是否为空
        AssertUtil.isTrue(ids == null || ids.length < 1, "请选择要删除的记录!");
        AssertUtil.isTrue(ids.length != saleChanceMapper.deleteBatch(ids), "删除营销机会数据失败!");
    }

    /**
     * 更新营销机会开发状态
     *  1、参数判断是否为空，且该条id数据存在
     *  2、修改开发状态
     *  3、执行更新操作，并判断执行结果
     * @param id
     * @param devResult
     */
    public void updateDevResult(Integer id, Integer devResult) {
        //参数判断
        AssertUtil.isTrue(id == null, "待更新记录不存在!");
        //通过id查询营销机会数据
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        //判断对象是否存在
        AssertUtil.isTrue(null == saleChance, "待更新记录不存在");
        //设置开发状态
        saleChance.setDevResult(devResult);
        saleChance.setUpdateDate(new Date());
        //执行更新操作判断执行结果
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) != 1, "开发状态更新失败!");
    }
}
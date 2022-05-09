package com.wang.crm.user.service;

import com.wang.crm.base.BaseService;
import com.wang.crm.exceptions.NoLoginException;
import com.wang.crm.user.dao.UserMapper;
import com.wang.crm.user.dao.UserRoleMapper;
import com.wang.crm.user.domain.User;
import com.wang.crm.user.domain.UserRole;
import com.wang.crm.user.model.UserModel;
import com.wang.crm.utils.AssertUtil;
import com.wang.crm.utils.Md5Util;
import com.wang.crm.utils.PhoneUtil;
import com.wang.crm.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User, Integer>{

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;


    /**
     * 七天免登录验用户名和密码
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel exemptLoginVerify(String userName, String userPwd) {
        //调用dao层
        User user = userMapper.queryUserByNameAndPassword(userName, userPwd);

        //判断用户对象是否为空,是则抛出异常
        if (user == null){
            throw new NoLoginException();
        }

        //返回构建用户对象
        return buildUserInfo(user);
    }

    /**
     * 用户登录：
     *  1、判断用户参数是否为空，如果为空抛出异常。
     *  2、调用dao层获取对象，如果返回对象为空，抛出异常
     *  3、比较客户端传过来的用户名是否和数据库中查出的用户名大小写是否一致，如果不一致抛出异常。
     * @param userName 用户名
     * @param userPwd 用户密码
     * user: 根据账号密码查询到的用户对象
     */
    public UserModel userLogin(String userName, String userPwd){
        //参数判断，检查客户端传过来的账号密码是否为空，是则抛出异常。
        checkLoginParams(userName, userPwd);

        //将客户端传过来的密码进行加密
        userPwd = Md5Util.encode(userPwd);

        //调用dao层
        User user = userMapper.queryUserByNameAndPassword(userName, userPwd);

        //判断用户对象是否为空,是则抛出异常
        AssertUtil.isTrue(user == null, "账号或密码有误!");

        //返回构建用户对象
        return buildUserInfo(user);
    }


    /**
     * 修改用户登录密码
     * @param userModel session中的UserModel对象
     * @param oldPwd    旧密码
     * @param newPwd    新密码
     * @param repeatPwd 确认密码
     */
    @Transactional
    public void updateUserPassword(UserModel userModel, String oldPwd, String newPwd, String repeatPwd) {
        //判断控制层传递过来的参数是否为空，有就抛出异常
        checkUpdatePwdParam(userModel, oldPwd, newPwd, repeatPwd);

        //判断新密码与确认密码是否相同，不相同就抛出异常
        AssertUtil.isTrue(!newPwd.equals(repeatPwd), "两次密码不一致!");


        //调用数据库访问层根据id查询用户
        User user = userMapper.selectByPrimaryKey(userModel.getId());

        //判断用户对象是否为空，为空就抛出异常
        AssertUtil.isTrue(user == null, "待更新用户不存在!");

        //判断客户端传递过来的旧密码是否与数据库中查询出来的密码相同，不相同就抛出异常。
        String encodeOldPwd = Md5Util.encode(oldPwd);
        AssertUtil.isTrue(!encodeOldPwd.equals(user.getUserPwd()), "原始密码不正确!");

        //判断新密码是否与原始原始密码相同，相同就抛出异常
        AssertUtil.isTrue(oldPwd.equals(newPwd), "新密码与原始密码不能相同!");

        //调用数据库访问层根据id修改密码
        User newUser = new User();
        newUser.setId(userModel.getId());
        newUser.setUserPwd(Md5Util.encode(newPwd));
        newUser.setUpdateDate(new Date());
        Integer result = userMapper.updateByPrimaryKeySelective(newUser);

        //判断影响记录条数是否为1，不为1就抛出异常
        AssertUtil.isTrue(result != 1, "修改密码失败!");
    }

    /**
     * 修改密码的参数校验
     * @param userModel    用户id
     * @param oldPwd    旧密码
     * @param newPwd    新密码
     * @param repeatPwd 重复密码
     */
    public void checkUpdatePwdParam(UserModel userModel, String oldPwd, String newPwd, String repeatPwd) {
        AssertUtil.isTrue(userModel == null, "非法请求!");
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd), "旧密码不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(newPwd), "新密码不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(repeatPwd), "确认密码不能为空!");
    }

    /**
     * 构建需要返回给客户端的用户对象
     * @param user
     * @return
     */
    public UserModel buildUserInfo(User user) {
        UserModel userModel = new UserModel();
        userModel.setId(user.getId());
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        userModel.setUserPwd(user.getUserPwd());
        return userModel;
    }

    /**
     * 判断用户名和密码是否为空
     * @param userName  用户名
     * @param userPwd   密码
     */
    public void checkLoginParams(String userName, String userPwd) {
        //AssertUtil工具类中的isTrue方法的参数有一个boolean值，这个值为true时会抛出异常。
        //lang3中的StringUtils方法可以判断传过去的参数是否为空，返回值为true代表是空，false则反之。
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户密码不能为空！");
    }


    /**
     * 查询所有销售人员
     * @return
     */
    public List<Map<String, Object>> queryAllSales(){
        return userMapper.queryAllSales();
    }

    /**
     * 用户添加：
     *      1、参数校验
     *          用户名  非空且唯一
     *          邮箱      非空
     *          手机号码    非空
     *
     *      2、设置默认参数
     *          isValid     1
     *          创建时间    系统当前时间
     *          默认密码    123456
     *
     *      3、执行添加操作并判断执行结果
     * @param user
     */
    @Transactional
    public void addUser(User user){
        //校验参数
        checkAddUserParams(user);
        //设置默认参数
        user.setIsValid(1);
        user.setCreateDate(new Date());
        //设置默认密码，默认密码为123456
        user.setUserPwd(Md5Util.encode("123456"));

        //执行添加操作并判断执行结果
        //返回的主键会自动注入到调用dao层方法时传递过去的user对象中。
        AssertUtil.isTrue(userMapper.insertSelective(user) != 1, "添加用户失败");

        //用户角色关联
        relationUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 关联用户角色
     * @param userId    用户id
     * @param roleIds   角色id字符串（包含多个角色id）
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //判断用户id是否为空且数据存在
        AssertUtil.isTrue(userId == null || userMapper.selectByPrimaryKey(userId) == null, "该用户不存在!");
        //根据用户id查询出用户所有的角色记录
        Integer count = userRoleMapper.countUserRoleByUserId(userId);
        if (count > 0){
            //如果角色记录存在则删除用户角色并判断执行结果
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "用户角色分配失败!");
        }
        /*
            当roleIds等于null或者空字符串
                如果是新增用户：说明没有为该用户添加任何角色
                如果是更新用户：说明取消了该用户的所有角色
         */
        if (StringUtils.isNotBlank(roleIds)){
            //将roleIds拆分成数组
            String[] split = roleIds.split(",");
            //创建集合
            List<UserRole> userRoleList = new ArrayList<>();
            //循环数组
            for (String roleId : split){
                //创建UserRole对象并设置参数
                UserRole userRole = new UserRole();
                userRole.setRoleId(Integer.valueOf(roleId));
                userRole.setUserId(userId);
                userRole.setCreateDate(new Date());

                //将UserRole对象放进集合中
                userRoleList.add(userRole);
            }
            //执行角色绑定并判断执行结果
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList) != userRoleList.size(), "用户角色分配失败!");
        }
    }

    /**
     * 添加用户时的参数判断
     * @param user
     */
    private void checkAddUserParams(User user) {
        //验证用户名是否为空
        AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()), "用户名不能为空!");
        //验证邮箱是否为空
        AssertUtil.isTrue(StringUtils.isBlank(user.getEmail()), "邮箱不能为空!");
        //验证手机号码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(user.getPhone()), "手机号码不能为空!");
        //验证手机号码格式是否正确
        AssertUtil.isTrue(!PhoneUtil.isMobile(user.getPhone()), "手机号码格式不正确!");
        //验证用户名是否存在
        /*
            如果是添加操作：查询出来的用户不等于空，说明用户名被占用。

            如果是更新操作：如果查询出来的记录中的id和传递过来的用户的id相同，那么说明查询出来的记录是本身，用户名可以相同，
                          如果查询出来的id和传递过来的id不相同，那么说明用户名被其它记录占用，用户名不可用
         */
        User temp = userMapper.selectByUserName(user.getUserName());
        AssertUtil.isTrue(temp != null && !(temp.getId().equals(user.getId())), "用户名已存在，请重新输入!");
    }

    /**
     * 用户修改操作
     *      1、参数判断
     *          id  判断id是否存在且数据存在
     *          用户名  非空且唯一
     *          邮箱      非空
     *          手机号码    非空
     *
     *      2、设置默认参数
     *          修改时间    系统当前时间
     *
     *      3、执行修改操作并判断执行结果
     *
     * @param user
     */
    @Transactional
    public void updateUser(User user){
        //判断id是否为空
        AssertUtil.isTrue(user.getId() == null, "待更新用户不存在");
        //判断id是否存在
        AssertUtil.isTrue(userMapper.selectByPrimaryKey(user.getId()) == null, "待更新用户不存在");
        //校验其余参数
        checkAddUserParams(user);
        //设置默认参数
        user.setUpdateDate(new Date());
        //执行修改操作并判断执行结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) != 1, "更新用户失败!");

        //用户角色绑定
        relationUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 删除用户（将is_valid字段更改为0）
     * @param ids
     */
    @Transactional
    public void deleteByIds(Integer[] ids) {
        //判断id数组是否为空，长度是否等于0
        AssertUtil.isTrue(ids == null || ids.length == 0, "待删除记录不存在!");
        //执行删除操作，判断执行结果
        AssertUtil.isTrue(userMapper.deleteBatch(ids) != ids.length, "删除用户失败!");

        //删除用户与角色的绑定
        for (Integer userId : ids){
            //根据用户id查询出用户所有的角色记录
            Integer count = userRoleMapper.countUserRoleByUserId(userId);
            if (count > 0){
                //如果角色记录存在则删除用户角色并判断执行结果
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "用户删除失败!");
            }
        }
    }


    /**
     * 查询所有客户经理
     * @return
     */
    public List<Map<String, Object>> queryCustomerManagers() {
        return userMapper.queryCustomerManagers();
    }

    /**
     * 更改用户信息
     *    1、参数判断
     *          id  不为空且数据存在
     *          用户名     未更改
     *          手机号码    非空且格式正确
     *          邮箱      非空
     *          真实姓名    非空
     *
     *    2、设置默认值
     *          更新时间    系统当前时间
     *
     *    3、执行更新操作并判断执行结果
     *
     * @param user
     */
    public void updateInfo(User user) {
        //参数校验
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(null == temp, "待更新用户不存在!");
        AssertUtil.isTrue(!temp.getUserName().equals(user.getUserName()), "用户名不可更改!");
        AssertUtil.isTrue(null == user.getPhone(), "手机号码不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(user.getPhone()), "手机号码格式不正确!");
        AssertUtil.isTrue(StringUtils.isBlank(user.getEmail()), "邮箱不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(user.getTrueName()), "真实姓名不能为空!");

        //设置默认值
        user.setUpdateDate(new Date());

        //执行更新操作并判断执行结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) != 1, "保存失败!");
    }
}

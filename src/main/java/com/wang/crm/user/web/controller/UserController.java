package com.wang.crm.user.web.controller;

import com.wang.crm.annotation.RequiredPermission;
import com.wang.crm.base.BaseController;
import com.wang.crm.base.ResultInfo;
import com.wang.crm.user.domain.User;
import com.wang.crm.user.model.UserModel;
import com.wang.crm.user.query.UserQuery;
import com.wang.crm.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    /**
     * 登录方法
     * @param userName  用户名
     * @param userPwd   用户密码
     * @return resultInfo登录信息
     */
    @PostMapping(value = "login")
    @ResponseBody
    public ResultInfo userLogin(HttpServletRequest request,HttpServletResponse response,
                                String userName,String userPwd,boolean rememberMe){

        ResultInfo resultInfo = new ResultInfo();
        //调用service层
        UserModel userModel = userService.userLogin(userName, userPwd);
        //将查询到的用户对象放入会话作用域中方便使用。
        HttpSession session = request.getSession();
        session.setAttribute("user", userModel);

        //判断用户是否选择了七天免登录，储存不同value的cookie
        Cookie cookie1 = new Cookie("userName", rememberMe ? userModel.getUserName() : "void");
        Cookie cookie2 = new Cookie("userPwd", rememberMe ? userModel.getUserPwd() : "void");
        cookie1.setPath(request.getContextPath());
        cookie2.setPath(request.getContextPath());
        cookie1.setMaxAge(60 * 60 * 24 * 7);
        cookie2.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        return resultInfo;
    }

    /**
     * 进入修改密码的视图
     * @return  修改密码的视图
     */
    @GetMapping(value = "updatePasswordView")
    public String passwordView(){
        return "user/password";
    }

    /**
     * 修改登录密码
     * @param session 会话作用于
     * @param oldPwd    旧密码
     * @param newPwd    新密码
     * @param repeatPwd 确认密码
     * @return  执行结果
     */
    @ResponseBody
    @PostMapping(value = "updateUserPassword")
    public ResultInfo updateUserPassword(HttpSession session, String oldPwd, String newPwd, String repeatPwd){
        ResultInfo resultInfo = new ResultInfo();
        //从会话作用域中取出UserModel对象
        UserModel userModel = (UserModel) session.getAttribute("user");
        userService.updateUserPassword(userModel, oldPwd, newPwd, repeatPwd);
        //将session中的user删除
        session.removeAttribute("user");
        /*try {
            //从会话作用域中取出UserModel对象
            UserModel userModel = (UserModel) session.getAttribute("user");
            userService.updateUserPassword(userModel, oldPwd, newPwd, repeatPwd);
            //将session中的user删除
            session.removeAttribute("user");
        } catch (ParamsException p) {
            resultInfo.setCode(p.getCode());
            resultInfo.setMsg(p.getMsg());
            p.printStackTrace();
        }catch (Exception e) {
            resultInfo.setCode(500);
            resultInfo.setMsg("修改密码失败!");
            e.printStackTrace();
        }*/
        return resultInfo;
    }

    /**
     * 退出系统
     * @param session
     * @return 登录页面视图
     */
    @GetMapping(value = "exit")
    public String userExit(HttpSession session, HttpServletRequest request){
        //将user从session中清除
        session.removeAttribute("user");
        return "index";
    }


    /**
     * 查询所有销售人员
     * @return
     */
    @RequestMapping(value = "queryAllSales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales(){
        return userService.queryAllSales();
    }


    /**
     * 分页多条件查询用户列表
     * @param userQuery
     * @return
     */
    @RequiredPermission(code = "601002")
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> selectByParams(UserQuery userQuery){
        return userService.queryByParamsForTable(userQuery);
    }


    /**
     * 进入用户管理首页视图
     * @return
     */
    @RequiredPermission(code = "6010")
    @GetMapping("index")
    public String index(){
        return "user/user";
    }



    /**
     * 添加用户
     * @param user
     * @return
     */
    @RequiredPermission(code = "601001")
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addUser(User user){
        userService.addUser(user);
        return success("添加用户成功");
    }

    /**
     * 更新用户
     * @param user
     * @return
     */
    @RequiredPermission(code = "601003")
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("更新用户成功!");
    }


    /**
     * 进入添加/修改用户的视图
     * @return
     */
    @RequestMapping("toAddOrUpdateUserPage")
    public String toUserPage(HttpServletRequest request, Integer id){
        //判断id是否为空，如果id不为空则是更新操作
        if (id != null){//更新操作
            User user = userService.selectByPrimaryKey(id);
            request.setAttribute("userInfo", user);
        }
        return "user/add_update";
    }


    /**
     * 删除用户
     * @param ids
     * @return
     */
    @RequiredPermission(code = "601004")
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteUserById(Integer[] ids){
        userService.deleteByIds(ids);
        return success("删除用户成功!");
    }


    /**
     * 查询所有客户经理
     * @return
     */
    @RequestMapping(value = "queryCustomerManagers")
    @ResponseBody
    public List<Map<String, Object>> queryCustomerManagers(){
        return userService.queryCustomerManagers();
    }


    /**
     * 进入用户信息视图
     * @return
     */
    @RequestMapping("toSettingPage")
    public String toSettingPage(HttpServletRequest request){
        //根据当前登录用户id查询用户
        UserModel userModel = (UserModel) request.getSession().getAttribute("user");
        User user = userService.selectByPrimaryKey(userModel.getId());
        request.setAttribute("user", user);
        return "user/setting";
    }


    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @PostMapping("updateInfo")
    @ResponseBody
    public ResultInfo updateInfo(User user){
        userService.updateInfo(user);
        return success("保存成功!");
    }
}

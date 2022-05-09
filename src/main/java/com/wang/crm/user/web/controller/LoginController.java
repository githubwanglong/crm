package com.wang.crm.user.web.controller;

import com.wang.crm.base.BaseController;
import com.wang.crm.user.model.UserModel;
import com.wang.crm.user.service.PermissionService;
import com.wang.crm.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class LoginController extends BaseController {


    @Resource
    private UserService userService;

    @Resource
    private PermissionService permissionService;

    /**
     * 系统登录页
     * @return
     */
    @RequestMapping({"index"})
    public String index(HttpServletRequest request){
        /*
            调用判断用户7天内登录时是否选择过7天免登录的方法。
            true表示用户7天内登录时选择过7天免登录，并且用户名和密码正确
            false表示用户7天内登录时没有选择过7天免登录
         */

        boolean flag = exemptLoginVerify(request);
        return flag ? "main" : "index";
    }

    // 系统界面欢迎页
    @RequestMapping("welcome")
    public String welcome() {
        return "welcome";
    }

    /**
     * 后端管理主页面
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest request){
        /*
            调用判断用户7天内登录时是否选择过7天免登录的方法。
            true表示用户7天内登录时选择过7天免登录，且用户名和密码正确、账号状态正常
            false表示用户7天内登录时没有选择过7天免登录
         */
        boolean flag = exemptLoginVerify(request);

        //判断用户是否已经登录
        UserModel userModel = (UserModel) request.getSession().getAttribute("user");

        //如果用户已经登录或者上次登录时选择了七天免登录则转发至主页面，如果都不满足则转发至登录页面。
        if (userModel != null || flag == true){

            //通过当前登录用户的id查询当前用户拥有的角色从而显示不同的菜单。
            List<String> permissions = permissionService.queryUserHasRoleHasPermission(userModel.getId());

            //将集合放入到session作用域中
            request.getSession().setAttribute("permissions", permissions);

            return "main";
        }
        return "index";
    }

    /**
     * //判断客户7天内登录时是否选择过7天免登录
     * @param request
     * @return
     */
    public boolean exemptLoginVerify(HttpServletRequest request){
        //判断客户7天内登录时是否选择过7天免登录的方法。
        Cookie[] cookies = request.getCookies();
        String userName = null;
        String userPwd = null;
        if (cookies != null){
            for (Cookie cookie : cookies){
                if ("userName".equals(cookie.getName())){
                    userName = cookie.getValue();
                }
                if ("userPwd".equals(cookie.getName())){
                    userPwd = cookie.getValue();
                }
            }
            if (userName != null && !"void".equals(userName)){
                //校验账号密码是否正确
                UserModel userModel = userService.exemptLoginVerify(userName, userPwd);
                //将UserModel对象存入session中
                request.getSession().setAttribute("user", userModel);
                //通过当前登录用户的id查询当前用户拥有的角色从而显示不同的菜单。
                List<String> permissions = permissionService.queryUserHasRoleHasPermission(userModel.getId());

                //将集合放入到session作用域中
                request.getSession().setAttribute("permissions", permissions);
                return true;
            }
        }
        return false;
    }
}

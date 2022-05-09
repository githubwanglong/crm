layui.use(['element', 'layer', 'layuimini','jquery','jquery_cookie'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        $ = layui.jquery_cookie($);

    //当前页面如果不是顶级窗口就以顶级窗口打开。
    $(function (){
        if (window.top != window.self){
            window.top.location = window.self.location;
        }
    })

    // 菜单初始化
    $('#layuiminiHomeTabIframe').html('<iframe width="100%" height="100%" frameborder="0"  src="welcome"></iframe>')
    layuimini.initTab();


    /**
     * 用户退出
     */
    $(".login-out").click(function (){
        layer.confirm('确定要退出系统吗?', {icon: 3, title:'系统提示'}, function(index){
            //关闭询问框
            layer.close(index);

            //发送请求到控制层，删除user
            window.location.href = ctx + "/user/exit";
        });
    })
});
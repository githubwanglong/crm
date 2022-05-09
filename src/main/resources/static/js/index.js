layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);



    //当前页面如果不是顶级窗口就以顶级窗口打开。
    $(function (){
        if (window.top != window.self){
            window.top.location = window.self.location;
        }
    })


    /**
     * 表单submit监听事件
     */
    form.on('submit(login)', function(data){
        console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}

        //表单内容的非空校验leyui已经完成了

        //发送ajax请求
        $.ajax({
            url:ctx+"/user/login",
            type:"post",
            data:{
                userName:data.field.username,
                userPwd:data.field.password,
                rememberMe:$("#rememberMe").prop("checked")
            },
            datatype:"json",
            success:function (result){
                //判断是否登录成功，code=200则表示登录成功
                if (result.code == 200){
                    //登录成功
                    /**
                     * 设置用户是登录状态
                     * 1、用session
                     * 2、利用cookie
                     */
                    //进行提示并跳转页面
                    window.location.href = ctx+"/main";
                }else{
                    //登录失败，进行提示
                    layer.msg(result.msg, {icon:5});
                }
            }
        })
        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });
});
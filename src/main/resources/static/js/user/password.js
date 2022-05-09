layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /**
     * 表单的submit()监听：触发时执行function。并对表单进行非空校验。
     *
     *      form.on('submit(提交按钮的lay-filter属性值)', function (data){
     *
     *       })
     */
    form.on('submit(saveBtn)', function (data){
        //所有表单元素的值
        console.log(data)

        //发送ajax请求，更改密码
        $.ajax({
            url:ctx+"/user/updateUserPassword",
            type:"post",
            data:{
                oldPwd:data.field.old_password,
                newPwd:data.field.new_password,
                repeatPwd:data.field.again_password
            },
            datatype:"json",
            success:function (result){
                //判断是否更改成功
                if(result.code == 200){
                    //修改成功，弹出提示消息并进行页面跳转让用户重新登录。
                    layer.msg("用户密码修改成功，系统将在3秒钟后退出...", function (){
                        window.parent.location.href = ctx+"/index";
                    });
                }else{
                    //修改失败，弹出提示信息
                    layer.msg(result.msg, {icon:5});
                }
            }
        })
        return false;//return false是为了阻止表单提交
    })
})
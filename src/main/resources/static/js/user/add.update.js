layui.use(['form', 'layer', 'formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    var formSelects = layui.formSelects;

    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function (){
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    })


    /**
     * 监听表单的submit事件
     * form.on("submit(按钮元素的lay-filter属性值)", function (data){

     * })
     */
    form.on("submit(addOrUpdateUser)", function (data){

        //提交数据时的加载层
        var index = layer.msg("数据提交中,请稍后...", {
            icon:16,    //图标
            time:false, //不关闭
            shade:0.5   //设置遮罩的透明度
        })


        var url = ctx+"/user/add";//添加操作

        //判断是执行添加操作还是更新操作，如果隐藏域中的id为空则是添加，不为空则是更新
        var id = $("[name=id]").val();
        if (id != null && id != ''){
            url = ctx+"/user/update";//更新操作
        }

        //发送ajax请求
        $.ajax({
            type:"post",
            url:url,
            data:data.field,
            datatype:"json",
            success:function (result){
                //判断是否添加成功，code=200说明添加成功
                if (result.code == 200){
                    //添加成功
                    //提示
                    layer.msg(result.msg, {icon:6});
                    //关闭加载层
                    layer.close(index);
                    //关闭弹出层
                    layer.closeAll("iframe");
                    //刷新父窗口，重新加载资源
                    parent.location.reload();
                }else{
                    //添加失败，进行提示
                    layer.msg(result.msg, {icon:5})
                }
            }
        })

        //阻止表单提交
        return false;
    })


    /**
     * 加载角色下拉框
     */
    formSelects.config("selectId", {
        type:'post',//请求方式
        searchUrl:ctx+'/role/queryAllRoles?userId='+$("[name=id]").val(),//请求地址
        keyName:'roleName',//下拉框中显示的文本内容，需要与返回数据中的key一致
        keyVal: 'id',//
    }, true)
    
});
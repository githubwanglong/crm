layui.use(['form', 'layer', 'formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    var formSelects = layui.formSelects;


    /**
     * 监听表单的submit事件
     * form.on("submit(按钮元素的lay-filter属性值)", function (data){

     * })
     */
    form.on("submit(saveBtn)", function (data){

        //提交数据时的加载层
        var index = layer.msg("数据提交中,请稍后...", {
            icon:16,    //图标
            time:false, //不关闭
            shade:0.5   //设置遮罩的透明度
        })


        //发送ajax请求
        $.ajax({
            type:"post",
            url:ctx+"/user/updateInfo",
            data:data.field,
            datatype:"json",
            success:function (result){
                //判断是否操作成功，code=200说明添加成功
                if (result.code == 200){
                    //操作成功
                    //提示
                    layer.msg(result.msg, {icon:6});
                    //关闭加载层
                    layer.close(index);
                }else{
                    //操作失败，进行提示
                    layer.msg(result.msg, {icon:5})
                }
            }
        })

        //阻止表单提交
        return false;
    })

});
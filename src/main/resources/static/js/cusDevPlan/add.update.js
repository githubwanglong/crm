layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    /**
     * 自动触发计划项内容单击事件
     */
    $("#planItem").trigger("focus")

    /**
     * 时间拾取器
     */
    layui.use('laydate', function(){
        var laydate = layui.laydate;

        //执行一个laydate实例
        laydate.render({
            elem: '#planDate', //指定元素
        });
    });

    /**
     *表单submit的监听事件
     */
    form.on('submit(addOrUpdateCusDevPlan)', function (data){

        //提交数据时的加载层
        var index = top.layer.msg("数据提交中,请稍后...", {
            icon:16,    //图标
            time:false, //不关闭
            shade:0.5   //设置遮罩的透明度
        })

        //发送ajax请求
        //得到所有的表单元素的值
        var formDate = data.field;

        var url = ctx+"/cus_dev_plan/add";

        /*
            判断时指定更新操作还是添加操作，
            如果<input name="id" type="hidden" th:value="${cusDevPlan?.id}"/> 的value为空则是添加，如果不为空则是更新
         */
        if ($("[name=id]").val() != null && $("[name=id]").val() != ''){//更新操作
            //更改url
            url = ctx+"/cus_dev_plan/update";
        }
        $.ajax({
            type:"post",
            url:url,
            data:formDate,
            datatype:"json",
            success:function (result){
                //判断是否添加成功，code=200说明添加成功
                if (result.code == 200){
                    //添加成功
                    //提示
                    top.layer.msg(result.msg, {icon:6});
                    //关闭加载层
                    top.layer.close(index);
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
     * 关闭弹出层
     */
    $("#closeBtn").click(function (){
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    })

});
layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    /**
     * 监听表单的submit事件
     * form.on("submit(按钮元素的lay-filter属性值)", function (data){

     * })
     */
    form.on("submit(addOrUpdateSaleChance)", function (data){

        //提交数据时的加载层
        var index = layer.msg("数据提交中,请稍后...", {
            icon:16,    //图标
            time:false, //不关闭
            shade:0.5   //设置遮罩的透明度
        })


        var url = ctx+"/sale_chance/add";//添加操作
        //通过营销机会的id来判断是进行添加操作还是进行更新操作
        //如果营销机会id为空是添加操作，如果营销机会id不为空则是更新操作
        //从隐藏域中获取id
        var saleChanceId = $("[name=id]").val();
        //判断id是否为空
        if (saleChanceId != null && saleChanceId != ''){
            //更改url，去后台执行更新操作
            url = ctx+"/sale_chance/update";
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
                    layer.msg("添加成功", {icon:6});
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
     * 关闭弹出层
     */
    $("#closeBtn").click(function (){
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    })


    /**
     * 加载指派人下拉框
     *
     */
    $.ajax({
        type:"get",
        url:ctx+"/user/queryAllSales",
        datatype: "json",
        success:function (data){
            //遍历返回的数据是否为null
            if (data != null){
                //获取隐藏域中的指派人
                var assignManId = $("#man").val()
                //遍历返回的数据
                for (i = 0; i < data.length; i++){
                    var opt = "";
                    //设置下拉选项
                    if (assignManId == data[i].id){
                        //设置下拉框默认选中指派人
                        opt = "<option value="+data[i].id+" selected>"+data[i].name+"</option>"
                    }else{
                        opt = "<option value="+data[i].id+">"+data[i].name+"</option>"
                    }
                    $("#assignMan").append(opt)
                }
            }
            //重新渲染下拉框的内容
            layui.form.render("select")
        }
    })
});
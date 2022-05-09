layui.use(['table','layer',"form"],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
        
    // 暂缓列表展示
    var  tableIns = table.render({
        elem: '#customerRepList',
        url : ctx+'/customer_rep/list?lossId='+$("input[name='id']").val(),
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "customerRepListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'序列',fixed:"true"},
            {field: 'measure', title: '暂缓措施',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#customerRepListBar"}
        ]]
    });


    /**
     * 监听头部工具栏事件
     * table.on('toolbar(数据表格lay-filter属性值)', function (data){
     * })
     */
    table.on('toolbar(customerReps)', function (data){
        //data.event：对应的元素上设置的lay-event属性值
        if (data.event == "add"){//添加暂缓
            //打开添加暂缓数据的对话框
            openAddOrUpdateReprieveDialog($("[name=id]").val())
        }else if(data.event == "confirm"){//确认流失
            //更新流失客户的流失状态
            updateCustomerLossState();
        }
        else if(data.event == "renew"){//确认流失
            //将流失客户恢复正常
            renewCustomerLossState();
        }
    })


    /**
     * 将流失客户恢复成正常状态
     */
    function renewCustomerLossState(){
        //弹出确认框询问用户是否恢复该客户
        layer.confirm("确定将当前客户恢复成未流失吗?", {icon:3, title:"暂缓管理"}, function (index) {

            //关闭确认框
            layer.close(index)

            $.ajax({
                url:ctx+"/customer_loss/renewCustomerLossStateById",
                type:"post",
                data:{
                    id:$("[name=id]").val(),//流失客户的id
                },
                datatype:"json",
                success:function (data){
                    //判断删除操作执行结果
                    if (data.code == 200){
                        //更新成功
                        layer.msg(data.msg, {icon:6});
                        //关闭弹出层
                        layer.closeAll("iframe")
                        //刷新父页面
                        parent.location.reload();
                    }else{
                        //更新失败
                        layer.msg(data.msg, {icon:5});
                    }
                }
            })
        })
    }



    /**
     * 更新流失客户的流失状态
     */
    function updateCustomerLossState(){
        //弹出确认框询问用户是否要流失该客户
        layer.confirm("确定标记当前客户为确认流失吗?", {icon:3, title:"暂缓管理"}, function (index){

            //关闭确认框
            layer.close(index)

            //prompt层，输入框
            layer.prompt({title: '请输入流失原因', formType: 2}, function(text, index){
                //关闭输入框
                layer.close(index);

                /*
                    发送请求到后台更新指定客户的流失装填
                    1、指定流失客户    流失客户ID（隐藏域中）
                    2、流失原因       输入框的内容（text）
                 */
                $.ajax({
                    url:ctx+"/customer_loss/updateCustomerLossStateById",
                    type:"post",
                    data:{
                        id:$("[name=id]").val(),//流失客户的id
                        lossReason:text //流失原因
                    },
                    datatype:"json",
                    success:function (data){
                        //判断删除操作执行结果
                        if (data.code == 200){
                            //更新成功
                            layer.msg(data.msg, {icon:6});
                            //关闭弹出层
                            layer.closeAll("iframe")
                            //刷新父页面
                            parent.location.reload();
                        }else{
                            //更新失败
                            layer.msg(data.msg, {icon:5});
                        }
                    }
                })
            });
        })
    }


    /**
     * 打开添加/修改暂缓数据的对话框
     */
    function openAddOrUpdateReprieveDialog(lossId, id){
        var title = "<h3>暂缓管理 — 添加暂缓</h3>";
        var url = ctx+"/customer_rep/toAddOrUpdateReprievePage?lossId="+lossId;

        //判断id是否为空，如果id不为空则是更新操作
        if (id != null && id != ''){
            title = "<h3>暂缓管理 — 更新暂缓</h3>";
            url += "&id="+id;
        }
        //iframe层
        layui.layer.open({
            //类型
            type: 2,
            //标题
            title: title,
            //宽高
            area: ['500px', '200px'],
            //地址
            content:url,
            //可以最大化和最小化
            maxmin:true
        });
    }



    /**
     * 监听行工具栏事件
     * table.on('toolbar(数据表格lay-filter属性值)', function (data){
     * })
     */
    table.on('tool(customerReps)', function (data){
        //data.event：对应的元素上设置的lay-filter属性值
        if (data.event == "edit"){//更新暂缓数据
            ////打开更新暂缓数据的对话框
            openAddOrUpdateReprieveDialog($("[name=id]").val(), data.data.id)
        }else if(data.event == "del"){//删除暂缓记录
            //弹出确认框询问用户是否要删除
            layer.confirm("确定要删除该条记录吗?", {icon:3, title:"暂缓管理"}, function (index){
                //关闭确认框
                layer.close(index)
                //发送ajax请求到后台删除记录
                $.ajax({
                    url:ctx+"/customer_rep/delete",
                    type:"post",
                    data:{
                        reprieveId:data.data.id
                    },
                    datatype:"json",
                    success:function (result){
                        //判断删除操作执行结果
                        if (result.code == 200){
                            //删除成功
                            layer.msg(result.msg, {icon:6});
                            //刷新表格
                            tableIns.reload();
                        }else{
                            //删除失败
                            layer.msg(result.msg, {icon:5});
                        }
                    }
                })
            })
        }
    })
});

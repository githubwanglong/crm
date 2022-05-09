layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 加载计划项数据表格
     */
    //当前营销机会的id
    var saleChanceId = $("[name=id]").val();

    var tableIns = table.render({
        id:'cusDevPlanTable'
        ,elem: '#cusDevPlanList' //容器元素的id属性值
        ,height: 'full-140' //容器的高度 full-差值
        ,cellMinWidth:98    //单元格的最小宽度
        ,url: ctx+'/cus_dev_plan/list?saleChanceId='+ saleChanceId  //访问数据的url，对应后台的接口
        ,page: true //开启分页
        ,limit:10   //每页默认显示条数
        ,limits:[10,20,30,40,50] //每页显示条数的可选项
        ,toolbar:"#toolbarDemo" //开启头部工具栏
        ,cols: [[ //表头
            //field：要求field属性值与返回的数据中对应的属性字段名一致否则无法赋值。
            //title：设置列的标题
            //sort：是否允许排序，默认为false
            //fixed：固定列
            //{type:'checkbox', fixed: 'center'}
            {field: 'id', title: '序列', sort: true, fixed: 'left',width:'80'}
            ,{field: 'planItem', title: '计划项', align:"center", fixed: 'left'}
            ,{field: 'planDate', title: '计划时间', align:"center"}
            ,{field: 'exeAffect', title: '执行效果', align:"center"}
            ,{field: 'createDate', title: '创建时间', align:"center"}
            ,{field: 'updateDate', title: '最近修改时间', align:"center"}
            ,{title: '操作', templet:'#cusDevPlanListBar', fixed:'right', align: 'center', minWidth:'130'}
        ]]
    });


    /**
     * 监听头部工具栏
     */
    table.on('toolbar(cusDevPlans)', function (data){
        //操作判断
        if (data.event == "add"){//添加计划项

            //打开添加或修改计划项的页面
            openAddOrUpdateCusDevPlanDialog()

        }else if (data.event == "success"){//开发成功

            //更新营销机会的开发状态
            updateSaleChanceDevResult(2);

        }else if (data.event == "failed"){//开发失败

            //更新营销机会的开发状态
            updateSaleChanceDevResult(3);
        }
    });


    /**
     * 更新营销机会的开发状态
     * @param devResult
     */
    function updateSaleChanceDevResult(devResult){
        //弹出询问框询问用户
        layer.confirm("确定要执行该操作吗?", {icon:3, title:"营销机会管理"}, function (index){
            //得到需要被更新的营销机会的id，通过隐藏域获取
            var id = $("[name=id]").val();
            //发送ajax请求到后台执行更新操作
            $.ajax({
                url:ctx+"/sale_chance/updateSaleChanceDevResult",
                type:"post",
                data:{
                    id:id,
                    devResult:devResult
                },
                datatype:"json",
                success:function (data){
                    //判断执行结果
                    if (data.code == 200){//删除成功
                        //进行提示
                        layer.msg(data.msg, {icon:6})
                        //关闭弹出层
                        layer.closeAll("iframe")
                        //刷新数据表格
                        parent.location.reload();
                    }else{//删除失败
                        //提示失败原因
                        layer.msg(data.msg, {icon:5})
                    }
                }
            })
        })
    }



    /**
     * 监听行工具栏
     *
     */
    table.on('tool(cusDevPlans)',function (data){
        if (data.event == "edit"){//编辑操作

            //打开添加或修改计划项的页面
            openAddOrUpdateCusDevPlanDialog(data.data.id)
        }else if (data.event == "del"){//删除操作
            //调用方法删除
            deleteCusDevPlan(data.data.id)
        }
    });


    /**
     * 删除开发计划项的方法
     */
    function deleteCusDevPlan(id){
        //弹出确认框询问用户是否执行删除操作
        layer.confirm("确定要删除计划项吗?", {icon:3, title:"开发项数据管理"}, function (index){
            //发送ajax请求到后台执行删除操作
            $.ajax({
                url:ctx+"/cus_dev_plan/delete",
                type:"post",
                data:{
                    id:id
                },
                datatype:"json",
                success:function (data){
                    //判断执行结果
                    if (data.code == 200){//删除成功
                        //进行提示
                        layer.msg(data.msg, {icon:6})
                        //刷新数据表格
                        tableIns.reload();
                    }else{//删除失败
                        //提示失败原因
                        layer.msg(data.msg, {icon:5})
                    }
                }
            })
        })
    }
    /**
     * 打开添加或修改计划项的页面
     */
    function openAddOrUpdateCusDevPlanDialog(id){
        var title = "计划项管理 — 添加计划项";
        var url = ctx+'/cus_dev_plan/toAddOrUpdateCusDevPlanPage?sId='+$("[name=id]").val();
        //判断计划项，id为空是添加，id不为空则为更新
        if (id != null && id != ''){//更新
            //更改标题
            title = "计划项管理 — 更新计划项";
            //追加id，后台用这个id查询计划项然后将计划项数据放入请求作用域中
            url += "&id="+id;
        }

        //iframe层
        layui.layer.open({
            //类型
            type: 2,
            //标题
            title: title,
            //宽高
            area: ['500px', '300px'],
            //地址
            content:url,
            //可以最大化和最小化
            maxmin:true
        });
    }



});

layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 加载数据表格
     */
    var tableIns = table.render({
        id:'saleChanceTable'
        ,elem: '#saleChanceList' //容器元素的id属性值
        ,height: 'full-140' //容器的高度 full-差值
        ,cellMinWidth:98    //单元格的最小宽度
        ,url: ctx+'/sale_chance/list'   //访问数据的url，对应后台的接口
        ,page: true //开启分页
        ,limit:10   //每页默认显示条数
        ,limits:[10,20,30,40,50,60] //每页显示条数的可选项
        ,toolbar:"#toolbarDemo" //开启头部工具栏
        ,cols: [[ //表头
            //field：要求field属性值与返回的数据中对应的属性字段名一致否则无法赋值。
            //title：设置列的标题
            //sort：是否允许排序，默认为false
            //fixed：固定列
            {type:'checkbox', fixed: 'center'}
            ,{field: 'id', title: '序列', sort: true, fixed: 'left',width:'80'}
            ,{field: 'customerName', title: '客户名称', align:"center", fixed: 'left'}
            ,{field: 'chanceSource', title: '机会来源', align:"center"}
            ,{field: 'devResult', title: '开发状态', align:"center", templet: function (data){
                //调用函数返回格式化的结果
                return formatDevResult(data.devResult);
             }}
            ,{field: 'cgjl', title: '成功几率', align:"center"}
            ,{field: 'overview', title: '概要', align:"center"}
            ,{field: 'linkMan', title: '联系人', align:"center"}
            ,{field: 'linkPhone', title: '联系电话', align:"center"}
            ,{field: 'description', title: '描述', align:"center"}
            ,{field: 'createMan', title: '创建人', align:"center"}
            ,{field: 'createDate', title: '创建时间', align:"center"}
            ,{field: 'state', title: '分配状态', align:"center", templet: function (data){
                //调用函数返回格式化的结果
                return formatState(data.state);
             }}
            ,{field: 'assignMan', title: '分配人', align:"center"}
            ,{field: 'assignTime', title: '分配时间', align:"center"}
            ,{field: 'updateDate', title: '最近修改时间', align:"center",width:'150'}
            ,{title: '操作', templet:'#saleChanceListBar', fixed:'right', align: 'center', minWidth:'130'}
        ]]
    });


    /**
     * 格式化分配状态的函数
     * 0 = 未分配
     * 1 = 已分配
     * 其它 = 未知
     * @param state
     */
    function formatState(state){
        if(state == 0){
            return "<div style='color: orange'>未分配</div>"
        }else if (state == 1){
            return "<div style='color: green'>已分配</div>"
        }else{
            return "<div style='color: blueviolet'>未知</div>"
        }
    }


    /**
     * 格式化开发状态的函数
     * 0 = 未开发
     * 1 = 开发中
     * 2 = 开发成功
     * 3 = 开发失败
     * 其它 = 未知
     * @param devResult
     */
    function formatDevResult(devResult){
        if(devResult == 0){
            return "<div style='color: orange'>未开发</div>"
        }else if (devResult == 1){
            return "<div style='color: #00b7ee'>开发中</div>"
        }else if (devResult == 2){
            return "<div style='color: darkgreen'>开发成功</div>"
        }else if (devResult == 3){
            return "<div style='color: red'>开发失败</div>"
        }else{
            return "<div style='color: blueviolet'>未知</div>"
        }
    }


    /**
     * 给搜索按钮绑定单击事件
     */
    $(".search_btn").click(function (){
        /**
         * 表格重载
         *  多条件查询
         */
        tableIns.reload({
            //需要传递给后端的参数
            where: { //设定异步数据接口的额外参数，任意设
                //查询参数，取文本框的值
                customerName: $("[name='customerName']").val(),//客户名称
                createMan: $("[name='createMan']").val(),//创建人
                state: $("[name='state']").val(),//分配状态
                devResult: $("[name='devResult']").val()//开发状态
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });


    /**
     * 监听头部工具栏事件
     * table.on('toolbar(数据表格lay-filter属性值)', function (data){
     * })
     */
    table.on('toolbar(saleChances)', function (data){
       //data.event：对应的元素上设置的lay-filter属性值
        if (data.event == "add"){
            //添加操作
            //打开添加营销机会的窗口
            openSaleChanceDialog();
        }else if(data.event == "del"){
            //删除操作
            deleteSaleChance(data);
        }
    })

    /**
     * 批量删除营销机会的方法
     * @param data
     */
    function deleteSaleChance(data){
        //获取数据表格中选中的数据   table.checkStatus('数据表格的id属性值')，，data.config.id可以直接获取
        var checkStatus = table.checkStatus(data.config.id);
        //判断是否有选中的记录
        var saleChanceData = checkStatus.data;
        if (saleChanceData.length < 1){
            //没有选中的数据
            layer.msg("请选择需要删除的数据!", {icon:5})
            return;
        }

        //弹出确认框询问用户是否要删除
        layer.confirm("确定要删除选中的记录吗?", {icon:3, title:"营销机会管理"}, function (index){
            //关闭确认框
            layer.close(index)
            //拼接参数，参数是数组 ?ids=1&ids=2&ids=3
            var ids = '';
            for (i = 0; i < saleChanceData.length; i++){
                if (i == saleChanceData.length - 1){
                    ids += 'ids='+saleChanceData[i].id
                }else{
                    ids += 'ids='+saleChanceData[i].id+'&'
                }
            }
            //发送ajax请求到后台删除记录
            $.ajax({
                url:ctx+"/sale_chance/delete",
                type:"post",
                data:ids,
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

    /**
     * 打开添加、修改营销机会的窗口
     *  如果saleChanceId为空就是添加操作
     *  如果saleChanceId不为空就是修改操作
     */
    function openSaleChanceDialog (saleChanceId){
        var title = "<h3>营销机会管理 — 添加营销机会</h3>"
        var url = ctx+"/sale_chance/toSaleChancePage"
        //判断营销机会id是否为空
        if (saleChanceId != null && saleChanceId != ''){
            //变更title
            title = "<h3>营销机会管理 — 更新营销机会</h3>"
            //变更url，发送请求到后台根据id获取营销机会
            url += "?saleChanceId=" + saleChanceId;
        }
        //iframe层
        layui.layer.open({
            //类型
            type: 2,
            //标题
            title: title,
            //宽高
            area: ['500px', '620px'],
            //地址
            content:url,
            //可以最大化和最小化
            maxmin:true
        });
    }

    /**
     * 行工具栏监听事件
     * table.on("tool(数据表格的lay-filter属性值)", function (data){
     * })
     *
     */
    table.on("tool(saleChances)", function (data){
        console.log(data)

        //判断类型
        if (data.event == "edit"){//编辑操作
            //得到营销机会记录的id
            var saleChanceId = data.data.id;
            //打开修改营销机会的窗口
            openSaleChanceDialog(saleChanceId);

        }else if (data.event == "del"){//删除操作
            //弹出确认框询问用户是否要删除
            layer.confirm("确定要删除该条记录吗?", {icon:3, title:"营销机会管理"}, function (index){
                //关闭确认框
                layer.close(index)
                //发送ajax请求到后台删除记录
                $.ajax({
                    url:ctx+"/sale_chance/delete",
                    type:"post",
                    data:{
                        ids:data.data.id
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

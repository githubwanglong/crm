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
        ,url: ctx+'/sale_chance/list?flag=1'   //访问数据的url，对应后台的接口，设置flag参数表示查询的是客户开发计划数据
        ,page: true //开启分页
        ,limit:10   //每页默认显示条数
        ,limits:[10,20,30,40,50,60] //每页显示条数的可选项
        ,toolbar:"#toolbarDemo" //开启头部工具栏
        ,cols: [[ //表头
            //field：要求field属性值与返回的数据中对应的属性字段名一致否则无法赋值。
            //title：设置列的标题
            //sort：是否允许排序，默认为false
            //fixed：固定列
            {field: 'id', title: '序列', sort: true, fixed: 'left',width:'80'}
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
            ,{field: 'updateDate', title: '最近修改时间', align:"center",width:'150'}
            ,{title: '操作', templet:'#op', fixed:'right', align: 'center', minWidth:'130'}
        ]]
    });



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
                devResult: $("[name='devResult']").val()//开发状态
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });


    /**
     * 行工具栏的监听
     *
     */
    table.on('tool(saleChances)', function (data){

        //判断类型
        if (data.event == 'dev'){//开发

            //打开计划项
            openCusDevPlanDialog("计划项数据开发", data.data.id);
        }else if(data.event == 'info'){//详情
            //打开详情
            openCusDevPlanDialog("计划项数据维护", data.data.id);
        }
    })


    /**
     * 打开计划项开发或详情页面
     */
    function openCusDevPlanDialog(title, id){

        //iframe层
        layui.layer.open({
            //类型
            type: 2,
            //标题
            title: title,
            //宽高
            area: ['750px', '600px'],
            //地址
            content:ctx+'/cus_dev_plan/toCusDevPlanPage?id='+id,
            //可以最大化和最小化
            maxmin:true
        });
    }
});

layui.use(['table','layer',"form"],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table,
        form = layui.form;

    //客户列表展示
    var  tableIns = table.render({
        elem: '#customerList',
        url : ctx+'/customer/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "customerListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'序列',fixed:"true"},
            {field: 'name', title: '客户名',align:"center"},
            {field: 'fr', title: '法人',  align:'center'},
            {field: 'khno', title: '客户编号', align:'center'},
            {field: 'area', title: '地区', align:'center'},
            {field: 'cusManager', title: '客户经理',  align:'center'},
            {field: 'myd', title: '满意度', align:'center'},
            {field: 'level', title: '客户级别', align:'center'},
            {field: 'xyd', title: '信用度', align:'center'},
            {field: 'address', title: '详细地址', align:'center'},
            {field: 'postCode', title: '邮编', align:'center'},
            {field: 'phone', title: '电话', align:'center'},
            {field: 'webSite', title: '网站', align:'center'},
            {field: 'fax', title: '传真', align:'center'},
            {field: 'zczj', title: '注册资金', align:'center'},
            {field: 'yyzzzch', title: '营业执照', align:'center'},
            {field: 'khyh', title: '开户行', align:'center'},
            {field: 'khzh', title: '开户账号', align:'center'},
            {field: 'gsdjh', title: '国税', align:'center'},
            {field: 'dsdjh', title: '地税', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'updateDate', title: '更新时间', align:'center'},
            {title: '操作', templet:'#customerListBar',fixed:"right",align:"center", minWidth:150}
        ]]
    });

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
                customerName: $("[name='name']").val(),//客户名称
                customerNo: $("[name='khno']").val(),//客户编号
                level: $("#level").val(),//客户级别
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
    table.on('toolbar(customers)', function (data){
        //data.event：对应的元素上设置的lay-event属性值
        if (data.event == "add"){//添加客户
            //打开添加/更新客户的窗口
            openAddOrupdateCustomerDialog()

        }else if(data.event == "link"){//联系人管理


        }else if(data.event == "recode"){//交往记录


        }else if(data.event == "order"){//客户的订单数据查看
            //获取被选中数据的相关信息
            var checkStatus = table.checkStatus(data.config.id)
            //打开客户订单的对话框（传递选中的记录）
            openCustomerOrderDialog(checkStatus.data)

        }

    })


    /**
     * 打开指定客户的订单记录对话框
     * @param data
     */
    function openCustomerOrderDialog(data){
        //判断是否选择了用户
        if (data.length == 0){
            layer.msg("请选择需要查看客户!", {icon:5});
            return;
        }

        //判断用户是否多选
        if (data.length > 1){
            layer.msg("暂不支持批量查看!", {icon:5});
            return;
        }

        //打开对话框
        layui.layer.open({
            //类型
            type: 2,
            //标题
            title: "<h3>客户管理 — 订单信息查看</h3>",
            //宽高
            area: ['800px', '700px'],
            //地址
            content:ctx+"/customer/openCustomerOrderPage?customerId="+data[0].id,
            //可以最大化和最小化
            maxmin:true
        });
    }

    /**
     * 打开添加/更新客户的窗口
     */
    function openAddOrupdateCustomerDialog(id){
        var title = "<h3>客户管理 — 添加客户信息</h3>";
        var url = ctx+"/customer/openAddOrupdateCustomerDialog";

        //判断id是否为空，如果id不为空则是更新操作
        if (id != null && id != ''){
            title = "<h3>客户管理 — 更新客户信息</h3>";
            url += "?customerId="+id;
        }
        //iframe层
        layui.layer.open({
            //类型
            type: 2,
            //标题
            title: title,
            //宽高
            area: ['700px', '650px'],
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
    table.on('tool(customers)', function (data){
        //data.event：对应的元素上设置的lay-filter属性值
        if (data.event == "edit"){//更新客户信息
            //打开添加/修改客户的对话框
            openAddOrupdateCustomerDialog(data.data.id)

        }else if(data.event == "del"){//删除客户信息
            //弹出确认框询问用户是否要删除
            layer.confirm("确定要删除该条记录吗?", {icon:3, title:"客户管理"}, function (index){
                //关闭确认框
                layer.close(index)
                //发送ajax请求到后台删除记录
                $.ajax({
                    url:ctx+"/customer/delete",
                    type:"post",
                    data:{
                        id:data.data.id
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

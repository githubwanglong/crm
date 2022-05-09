layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 用户列表展示
     */
    var  tableIns = table.render({
        id : "userTable"
        ,elem: '#userList' //容器元素的id属性值
        ,height: 'full-140' //容器的高度 full-差值
        ,cellMinWidth:98    //单元格的最小宽度
        ,url: ctx+'/user/list'   //访问数据的url，对应后台的接口
        ,page: true //开启分页
        ,limit:10   //每页默认显示条数
        ,limits:[10,20,30,40,50,60] //每页显示条数的可选项
        ,toolbar:"#toolbarDemo" //开启头部工具栏
        ,cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'序列',fixed:"true", width:80},
            {field: 'userName', title: '用户名', minWidth:50, align:"center"},
            {field: 'trueName', title: '真实姓名', align:'center'},
            {field: 'phone', title: '用户电话', minWidth:100, align:'center'},
            {field: 'email', title: '用户邮箱', minWidth:100, align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#userListBar',fixed:"right",align:"center"}
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
                userName: $("[name='userName']").val(),//用户名
                trueName: $("[name='trueName']").val(),//真实姓名
                email: $("[name='email']").val(),//邮箱
                phone: $("[name='phone']").val(),//手机号码
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
    table.on('toolbar(users)', function (data){
        //data.event：对应的元素上设置的lay-event属性值
        if (data.event == "add"){
            //添加用户
            //打开添加/修改用户的对话框
            openAddOrUpdateUser()
        }else if(data.event == "del"){
            //调用删除多个用户的方法
            deleteUser(data)
        }
    })


    /**
     * 批量删用户的方法
     * @param data
     */
    function deleteUser(data){
        //获取数据表格中选中的数据   table.checkStatus('数据表格的id属性值')，data.config.id可以直接获取
        var checkStatus = table.checkStatus(data.config.id);
        //判断是否有选中的记录
        var userData = checkStatus.data;
        if (userData.length < 1){
            //没有选中的数据
            layer.msg("请选择需要删除的数据!", {icon:5})
            return;
        }

        //弹出确认框询问用户是否要删除
        layer.confirm("确定要删除选中的记录吗?", {icon:3, title:"用户管理"}, function (index){
            //关闭确认框
            layer.close(index)
            //拼接参数，参数是数组 ?ids=1&ids=2&ids=3
            var ids = '';
            for (i = 0; i < userData.length; i++){
                if (i == userData.length - 1){
                    ids += 'ids='+userData[i].id
                }else{
                    ids += 'ids='+userData[i].id+'&'
                }
            }
            //发送ajax请求到后台删除记录
            $.ajax({
                url:ctx+"/user/delete",
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
     * 打开添加/修改用户的对话框
     */
    function openAddOrUpdateUser(id){
        var title = "<h3>用户管理 — 添加用户</h3>";
        var url = ctx+"/user/toAddOrUpdateUserPage";

        //判断id是否为空，如果id不为空则是更新操作
        if (id != null && id != ''){
            title = "<h3>用户管理 — 更新用户</h3>";
            url += "?id="+id;
        }
        //iframe层
        layui.layer.open({
            //类型
            type: 2,
            //标题
            title: title,
            //宽高
            area: ['650px', '400px'],
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
    table.on('tool(users)', function (data){
        //data.event：对应的元素上设置的lay-filter属性值
        if (data.event == "edit"){//更新用户
            //打开添加/修改用户的对话框
            openAddOrUpdateUser(data.data.id)
        }else if(data.event == "del"){
            //弹出确认框询问用户是否要删除
            layer.confirm("确定要删除该条记录吗?", {icon:3, title:"用户管理"}, function (index){
                //关闭确认框
                layer.close(index)
                //发送ajax请求到后台删除记录
                $.ajax({
                    url:ctx+"/user/delete",
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
layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 用户列表展示
     */
    var  tableIns = table.render({
        id : "roleTable"
        ,elem: '#roleList' //容器元素的id属性值
        ,height: 'full-140' //容器的高度 full-差值
        ,cellMinWidth:98    //单元格的最小宽度
        ,url: ctx+'/role/list'   //访问数据的url，对应后台的接口
        ,page: true //开启分页
        ,limit:10   //每页默认显示条数
        ,limits:[10,20,30,40,50,60] //每页显示条数的可选项
        ,toolbar:"#toolbarDemo" //开启头部工具栏
        ,cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'序列',fixed:"true", width:80},
            {field: 'roleName', title: '角色名称', minWidth:50, align:"center"},
            {field: 'roleRemark', title: '角色备注', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#roleListBar',fixed:"right",align:"center"}
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
                roleName: $("[name='roleName']").val(),//用户名
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
    table.on('toolbar(roles)', function (data){
        //data.event：对应的元素上设置的lay-event属性值
        if (data.event == "add"){
            //添加角色
            //打开添加/修改角色的对话框
            openAddOrUpdateRole()
        }else if(data.event == "grant"){
            //授权
            //获取数据表格选中的记录
            var checkStatus = table.checkStatus(data.config.id)
            //调用打开授权对话框的方法，将数据表格中选中的数据传过去
            openGrantDialog(checkStatus.data)
        }
    })


    /**
     * 打开授权页面
     * @param data
     */
    function openGrantDialog(data){
        //判断是否选择了角色记录
        if (data.length == 0){
            layer.msg("请选择要授权的角色!", {icon:5});
            return;
        }

        //每次授权只能选择一个角色
        if (data.length > 1){
            layer.msg("暂不支持批量角色授权!", {icon:5});
            return;
        }

        var url = ctx + '/module/toGrantPage?roleId='+data[0].id;
        var title = '<h3>角色管理 — 角色授权</h3>';

        //iframe层
        layui.layer.open({
            //类型
            type: 2,
            //标题
            title: title,
            //宽高
            area: ['500px', '700px'],
            //地址
            content:url,
            //可以最大化和最小化
            maxmin:true
        });
    }

    /**
     * 打开添加/修改角色的对话框
     */
    function openAddOrUpdateRole(id){
        var title = "<h3>角色管理 — 添加角色</h3>";
        var url = ctx+"/role/toAddOrUpdateRolePage";

        //判断id是否为空，如果id不为空则是更新操作
        if (id != null && id != ''){
            title = "<h3>角色管理 — 更新角色</h3>";
            url += "?roleId="+id;
        }
        //iframe层
        layui.layer.open({
            //类型
            type: 2,
            //标题
            title: title,
            //宽高
            area: ['500px', '400px'],
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
    table.on('tool(roles)', function (data){
        //data.event：对应的元素上设置的lay-filter属性值
        if (data.event == "edit"){//更新用户
            //打开添加/修改角色的对话框
            openAddOrUpdateRole(data.data.id)
        }else if(data.event == "del"){
            //弹出确认框询问用户是否要删除
            layer.confirm("确定要删除该条记录吗?", {icon:3, title:"角色管理"}, function (index){
                //关闭确认框
                layer.close(index)
                //发送ajax请求到后台删除记录
                $.ajax({
                    url:ctx+"/role/delete",
                    type:"post",
                    data:{
                        roleId:data.data.id
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
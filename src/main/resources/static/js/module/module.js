layui.use(['table', 'treetable'], function () {
    var $ = layui.jquery;
    var table = layui.table;
    var treeTable = layui.treetable;

    // 渲染表格
    treeTable.render({
        treeColIndex: 1,
        treeSpid: -1,
        treeIdName: 'id',
        treePidName: 'parentId',
        elem: '#munu-table',
        url: ctx+'/module/list',
        toolbar: "#toolbarDemo",
        treeDefaultClose:true,
        page: true,
        cols: [[
            {field: 'id', title: '序列'},
            {field: 'moduleName', minWidth: 100, title: '菜单名称'},
            {field: 'optValue', title: '权限码'},
            {field: 'url', title: '菜单url'},
            {field: 'createDate', title: '创建时间'},
            {field: 'updateDate', title: '更新时间'},
            {
                field: 'grade', width: 80, align: 'center', templet: function (d) {
                    if (d.grade == 0) {
                        return '<span class="layui-badge layui-bg-blue">目录</span>';
                    }
                    if(d.grade==1){
                        return '<span class="layui-badge-rim">菜单</span>';
                    }
                    if (d.grade == 2) {
                        return '<span class="layui-badge layui-bg-gray">按钮</span>';
                    }
                }, title: '类型'
            },
            {templet: '#auth-state', width: 180, align: 'center', title: '操作'}
        ]],
        done: function () {
            layer.closeAll('loading');
        }
    });


    /**
     * 监听头工具栏
     */
    table.on("toolbar(munu-table)", function (data){
        //判断动作
        if (data.event == "expand"){//全部展开

            treeTable.expandAll("#munu-table");

        }else if (data.event == "fold"){//全部折叠

            treeTable.foldAll("#munu-table");

        }else if (data.event == "add"){//添加目录
            //添加目录的方法
            openAddModuleDialog(0, -1);
        }
    })


    /**
     * 打开添加目录的对话框
     */
    function openAddModuleDialog(grade, parentId){
        var title = "<h3>资源管理 — 添加资源</h3>";
        var url = ctx + "/module/toAddModulePage?grade=" + grade + "&parentId=" + parentId;
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
     * 监听行工具栏
     */
    table.on("tool(munu-table)", function (data){
        //判断动作
        if (data.event == "add"){//添加子项
            //判断当前的层级，如果是三级菜单就不能添加子项
            if (data.data.grade == 2){
                layer.msg("暂不支持添加四级菜单", {icon:5})
                return;
            }

            //一级|二级菜单，grade=当前层级+1，parentId=当前资源的id
            openAddModuleDialog(data.data.grade+1, data.data.id)

        }else if (data.event == "edit"){//修改

            //更新资源
            openUpdateModuleDialog(data.data.id)

        }else if (data.event == "del"){//删除
            //删除资源
            deleteModule(data.data.id)
        }
    })


    /**
     * 删除资源
     * @param id
     */
    function deleteModule(id){
        //弹出确认框
        layer.confirm("确定要删除该资源吗?", {icon: 3, title:"资源管理"},function (){
            //确认删除，发送ajax到后台
            $.ajax({
                url:ctx+"/module/delete?id="+id,
                type:"post",
                datatype:"json",
                success:function (data){
                    //判断执行结果
                    if (data.code == 200){//删除成功
                        layer.msg(data.msg, {icon:6})
                        //刷新页面
                        window.location.reload();
                    }else{//删除失败
                        layer.msg(data.msg, {icon:5})
                    }
                }
            })
        })
    }


    /**
     * 更新资源
     */
    function openUpdateModuleDialog(id){
        var title = "<h3>资源管理 — 更新资源</h3>";
        var url = ctx + "/module/toUpdateModulePage?id=" + id;
        //iframe层
        layui.layer.open({
            //类型
            type: 2,
            //标题
            title: title,
            //宽高
            area: ['650px', '600px'],
            //地址
            content:url,
            //可以最大化和最小化
            maxmin:true
        });
    }
});
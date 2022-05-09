layui.use(['table','layer',"form"],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    //服务列表展示
    table.render({
        elem: '#customerServeList',
        url : ctx+'/customer_serve/list?state=fw_001',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "customerServeListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'序列',fixed:"true", width:80},
            {field: 'customer', title: '客户名称', minWidth:50, align:"center"},
            {field: 'dicValue', title: '服务类型', minWidth:100, align:'center'},
            {field: 'overview', title: '概要信息', align:'center'},
            {field: 'createPeople', title: '创建人', minWidth:100, align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
        ]]
    });

    // 多条件搜索
    $(".search_btn").on("click",function(){
        table.reload("customerServeListTable",{
            page: {
                curr: 1 //重新从第 1 页开始
            },
            where: {
                customer: $("input[name='customer']").val(),  //客户名
                serveType: $("#type").val()  //服务类型
            }
        })
    });


    /**
     * 监听头部工具栏事件
     * table.on('toolbar(数据表格lay-filter属性值)', function (data){
     * })
     */
    table.on('toolbar(customerServes)', function (data){
        //data.event：对应的元素上设置的lay-event属性值
        if (data.event == "add"){//添加服务
            //打开添加服务的对话框
            openAddCustomerServeDialog()
        }
    })


    /**
     * 打开添加服务的对话框
     */
    function openAddCustomerServeDialog(){
        //打开对话框
        layui.layer.open({
            //类型
            type: 2,
            //标题
            title: "<h3>服务管理 — 添加服务</h3>",
            //宽高
            area: ['700px', '500px'],
            //地址
            content:ctx+"/customer_serve/toAddCustomerServePage",
            //可以最大化和最小化
            maxmin:true
        });
    }

});

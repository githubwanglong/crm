$(function (){
    //加载树形结构
    loadModuleData()
})


var zTreeObj;


/**
 * 加载树形结构方法
 */
function loadModuleData(){
    //配置信息对象 zTree的参数配置

    var setting = {
        //使用复选框
        check:{
            enable:true
        },

        //使用简单的json数据
        data:{
          simpleData:{
              enable:true
          }
        },
        //绑定函数
        callback:{
            //onCheck函数：当checkbox/radio被选中或被取消选中时触发的函数。
            onCheck:zTreeOnCheck
        }
    }

    //数据
    //通过ajax查询资源列表
    $.ajax({
        //在查询所有资源列表时将角色id传递到后台，利用角色id判断当前角色已经拥有了哪些资源权限。
        url:ctx + "/module/queryAllModules",
        type:"get",
        data:{
            roleId:$("#roleId").val()
        },
        datatype:"json",
        success:function (data){
            //加载zTree树插件
            zTreeObj = $.fn.zTree.init($("#test1"), setting, data)
        }
    })
}


/**
 * 当单选框或复选框选中或被取消选中时的回调函数
 *
 */

function zTreeOnCheck(event, treeId, treeNode){
    //getCheckedNodes(true):获取所有被勾选的节点集合。
    var nodes = zTreeObj.getCheckedNodes(true)

    //判断并遍历选中的节点集合
    //获取所有资源的id值，moduleIds=1&moduleIds=2&moduleIds=3
    if (nodes.length > 0){
        var moduleIds = '';
        //遍历节点集合，获取资源的id值
        for (var i = 0; i < nodes.length; i++){
            moduleIds += 'moduleIds='+nodes[i].id
            if (i != nodes.length - 1){
                moduleIds += '&'
            }
        }
    }

    //获取需要授权的角色id
    var roleId = $("#roleId").val();

    //发送ajax请求执行角色授权操作
    $.ajax({
        url:ctx + "/role/addGrant",
        type:"post",
        data:moduleIds+"&roleId="+roleId,
        datatype:"json",
        success:function (data){
            console.log(data)
        }
    })
}
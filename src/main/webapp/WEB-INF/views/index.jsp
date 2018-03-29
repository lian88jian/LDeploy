<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>客户端列表</title>
	<meta name="description" content="Creating a table with Bootstrap. Learn how to use Bootstrap toolkit to create Tables with examples.">
	<link href="http://apps.bdimg.com/libs/bootstrap/3.3.4/css/bootstrap.css" rel="stylesheet">
</head>
<style>
.table th, .table td {
	text-align: center;
}
</style>
<body>

	<table class="table table-bordered table-hover">
		<thead>
			<tr>
				<th colspan="4">客户端</th>
				<th colspan="3">项目</th>
				<th colspan="*"></th>
			</tr>
			<tr>
				<th>名称</th>
				<th>ip</th>
				<th>状态</th>
				<th>最后在线</th>
				<th>类型</th>
				<th>状态</th>
				<th>版本</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody data-bind="foreach: clients">
			<tr>
				<td data-bind="text: clientName"></td>
				<td data-bind="text: ip"></td>
				<td data-bind="text: aliveHtml"></td>
				<td data-bind="text: lastHeartBeatTime"></td>
				<td data-bind="text: type"></td>
				<td data-bind="text: state"></td>
				<td data-bind="text: version"></td>
				<td>
					<div class="btn-group" data-bind="visible: alive()">
						<button type="button" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown">
							项目 <span class="caret"></span>
						</button>
						<ul class="dropdown-menu" role="menu">
							<li><a href="#" data-bind="click: chooseVersion">一键部署</a></li>
							<li><a href="#" data-bind="click: chooseVersion, visible: state() == '运行中'">停止</a></li>
						</ul>
					</div>
				</td>
			</tr>
		</tbody>
	</table>
	<!-- 版本选择 模态框（Modal） -->
	<div class="modal fade" id="versionModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	                <h4 class="modal-title" id="myModalLabel" data-bind="text: currentClientType"></h4>
	            </div>
	            <div class="modal-body">
					<select id="versionSel" class="form-control" data-bind="options: versionList">
					</select>
				</div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	                <button type="button" class="btn btn-primary" data-bind="click: startProject">确定</button>
	            </div>
	        </div>
	    </div>
	</div>
	<script type="text/javascript" src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
	<script type="text/javascript" src="http://apps.bdimg.com/libs/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="http://apps.bdimg.com/libs/knockout.js/3.1.0/knockout.min.js"></script>
	<script type="text/javascript">
		$(function(){
			var Client = function() {
				
				var _self = this;
				this.type = ko.observable();
				this.clientName = ko.observable();
				this.ip = ko.observable();
				this.alive = ko.observable();
				this.aliveHtml = ko.computed(function(){
					return _self.alive() ? "在线" : "离线";
				});
				this.lastHeartBeatTime = ko.observable();
				this.state = ko.observable();
				this.version = ko.observable();
				//更新数据
				this.update = function(data){
					_self.clientName(data.clientName);
					_self.ip(data.ip);
					_self.type(data.type);
					_self.alive(data.clientThread && data.clientThread.alive);
					_self.lastHeartBeatTime(data.clientThread && data.clientThread.lastHeartBeatTime);
					if(data.clientThread && data.clientThread.alive){
						_self.state(data.clientThread.projectRuning ? "运行中":"未启动");
					} else {
						_self.state("未知");
					}
					_self.version(data.clientThread && data.clientThread.projectVersion);
				}
				//选择版本
				this.chooseVersion = function(){
					$.getJSON("/LDeploy/" + _self.type() + "/getVersionList", function(res) {
						if(res.success){
							viewModel.currentClientType(_self.type());
							viewModel.currentClientName(_self.clientName());
							viewModel.versionList.removeAll();
							res.data.forEach((_v) => viewModel.versionList.push(_v));
							$('#versionModal').modal('show');
						} else {
							console.log(res);
							alert("get version list error");
						}
					});
				}
			}
			var ViewModel = function(){
				
				this.clients = ko.observableArray([]);
				
				this.versionList = ko.observableArray();
				
				this.currentClientType = ko.observable();
				this.currentClientName = ko.observable();
				
				this.getClient = function(clientName){
					for(var i = 0; i < this.clients().length; i ++){
						if(this.clients()[i].clientName() == clientName) {
							return this.clients()[i];
						}
					}
					var newClient = new Client();
					this.clients.push(newClient);
					return newClient;
				}
				
				this.startProject = function(){
					$.post("/LDeploy/oneKeyDeploy", {
						clientName: this.currentClientName,
						version: $('#versionSel').val()
					},function(res){
						if(res.success){
							alert('success');
						}else{
							alert(res.msg);
						}
					});
				};
			}
			var viewModel = new ViewModel();
			ko.applyBindings(viewModel);
			
			function refreshData(){
				$.post("/LDeploy/getClientList", {}, function(res){
					if(!res.success){
						alert("get client list error");
						return ;
					}
					res.data.forEach(function(record){
						var client = viewModel.getClient(record.clientName);
						client.update(record);
					});
				});
			}
			refreshData();
			setInterval(refreshData, 5000);
		});
	</script>
</body>
</html>
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

	<table class="table table-striped">
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
				<td data-bind=""></td>
				<td>
					<div class="btn-group" data-bind="visible: alive() && state() == '未启动'">
						<button type="button" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown">
							项目 <span class="caret"></span>
						</button>
						<ul class="dropdown-menu" role="menu">
							<li><a href="#" data-bind="click: startProject">启动</a></li>
							<li><a href="#" data-bind="click: startProject">停止</a></li>
						</ul>
					</div>
				</td>
			</tr>
		</tbody>
	</table>
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
				}
				
				this.startProject = function(){
					alert(_self.clientName());
				}
			}
			var ViewModel = function(){
				
				this.clients = ko.observableArray([]);
			
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
			}
			var viewModel = new ViewModel();
			ko.applyBindings(viewModel);
			
			function refreshData(){
				$.post("/LDeploy/getClientList", {}, function(res){
					if(!res.success){
						alert("数据加载失败");
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
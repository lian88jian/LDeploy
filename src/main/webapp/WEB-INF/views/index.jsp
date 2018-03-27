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
				<th class="text-center">名称</th>
				<th>ip</th>
				<th>类型</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
	<script type="text/javascript" src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
	<script type="text/javascript">
		$(function(){
			$.post("/LDeploy/getClientList", {}, function(res){
				if(!res.success){
					alert("数据加载失败");
					return ;
				}
				res.data.forEach(function(record){
					$('tbody').append(
						'<tr>' +
						'	<td class="text-center">' + record.clientName + '</th>' +
						'	<td>' + record.ip + '</th>' +
						'	<td>' + record.type + '</th>' +
						'	<td>' + '' + '</th>' +
						'</tr>'
					);
				});
			});
		});
	</script>
</body>
</html>
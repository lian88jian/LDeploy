package per.lian.web;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import per.lian.deploy.server.ClientInfo;
import per.lian.deploy.server.SocketServer;
import per.lian.web.core.BaseResult;

@Controller
@RequestMapping
public class IndexController {
	
	@RequestMapping("/")
	public ModelAndView index() {
		
		ModelAndView mav = new ModelAndView("index");
		
		return mav;
	}
	
	@ResponseBody
	@RequestMapping("getClientList")
	public BaseResult getClientList() {
		
		return BaseResult.getSuccess(SocketServer.getInstance().getClientList());
	}
	
	@ResponseBody
	@RequestMapping("{projectType}/getVersionList")
	public BaseResult getVersionList(@PathVariable("projectType") String projectType) {
		
		List<String> versionList = SocketServer.getVersionList(projectType);
		return BaseResult.getSuccess(versionList);
	}
	
	@ResponseBody
	@RequestMapping("{clientName}/stop")
	public BaseResult stop(@PathVariable("clientName")String clientName) {
		
		ClientInfo clientInfo = SocketServer.getInstance().getClientByName(clientName);
		if(clientInfo.getClientThread() == null || !clientInfo.getClientThread().isAlive()) {
			return BaseResult.getFailWithMsg("socket client not online");
		}
		try {
			clientInfo.getClientThread().sendStopProject();
		} catch (Exception e) {
			BaseResult.getFailWithMsg(e);
		}
		return BaseResult.getSuccess();
	}
	
	@ResponseBody
	@RequestMapping("oneKeyDeploy")
	public BaseResult oneKeyDeploy(String clientName, String version) {
		
		if(StringUtils.isAnyEmpty(clientName, version)) {
			return BaseResult.getFailWithMsg("request parameters not correct");
		}
		
		ClientInfo clientInfo = SocketServer.getInstance().getClientByName(clientName);
		if(clientInfo.getClientThread() == null || !clientInfo.getClientThread().isAlive()) {
			return BaseResult.getFailWithMsg("socket client not online");
		}
		try {
			clientInfo.getClientThread().oneKeyDeploy(version);
		} catch (Exception e) {
			BaseResult.getFailWithMsg(e);
		}
		return BaseResult.getSuccess();
	}
}

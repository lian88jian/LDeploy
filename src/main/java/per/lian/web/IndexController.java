package per.lian.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
}

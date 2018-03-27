package per.lian.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import per.lian.deploy.server.SocketServer;

public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		if(!SocketServer.getInstance().isAlive()) {
			SocketServer.getInstance().start();
		}
	}

}

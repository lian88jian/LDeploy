package per.lian.deploy.client;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import per.lian.deploy.pojo.SocketData;

/**
 * 心跳线程
 * @author goalsword
 *
 */
public class HeartBeatThread extends Thread {
	
	private SocketClient socketClient;
	private long delay;
	private ObjectOutputStream socketOut;

	public HeartBeatThread(SocketClient socketClient, long delay) {
		
		this.socketClient = socketClient;
		this.delay = delay;
	}
	
	public void init(ObjectOutputStream out) throws Exception {
		
		socketOut = out;
	}

	@Override
	public void run() {
		
		while(true){
			
			try {
				
				socketOut.writeObject(SocketData.CLIENT_CMD_HEART());
			} catch (Exception e) {
				
				socketClient.connect();
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}


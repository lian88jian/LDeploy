package per.lian.deploy.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

import per.lian.deploy.pojo.SocketData;
import per.lian.deploy.pojo.SocketDataType;
import per.lian.utils.FileUtil;

public class ClientReadThread extends Thread implements SocketDataType {

	private static ClientReadThread instance;
	private ObjectInputStream socketIn;
	private CommandThread commandThread;
	private ObjectOutputStream out;

	public static ClientReadThread getInstance() {
		if (instance == null) {
			instance = new ClientReadThread();
		}
		return instance;
	}

	private ClientReadThread() {

		commandThread = CommandThread.getInstance();
	}

	public void init(ObjectOutputStream out, InputStream in) throws Exception {
		this.socketIn = new ObjectInputStream(in);
		this.out = out;
	}

	@Override
	public void run() {

		while (true) {
			try {
				
				SocketData socketData = (SocketData) socketIn.readObject();
				_handleServerData(socketData);
			} catch (Exception e) {
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	

	public void sendSocketData(SocketData socketData) throws Exception {
		out.writeObject(socketData);
	}

	private void _handleServerData(SocketData socketData) throws Exception {

		switch (socketData.getType()) {
		case SERVER_CMD:
			commandThread.execute(socketData.getStringData());
			break;
		case SERVER_FILE:
			FileUtil.createFileWithBytes("F:\\temps\\tmp\\" + socketData.getMsg_2(), socketData.getMsg_1(), socketData.getData());
			break;
		case SERVER_SHUTDOWN:
			System.err.println("服务端要求关闭程序");
			SocketClient.getInstance().shutdown();
			break;
		case SERVER_ONEKEY_DEPLOY:
			String clientType = socketData.getMsg_1();
			if(!SocketClient.ClientType.equals(clientType)) {
				out.writeObject(new SocketData(CLIENT_ERROR, "服务端与客户端类型不一致"));
			}
			String clientVersion = socketData.getMsg_2();
			ClientFileManager.checkProjectFiles(FLOW_ONE_KEY_DEPLOY, clientVersion);
			break;
		}
	}
}

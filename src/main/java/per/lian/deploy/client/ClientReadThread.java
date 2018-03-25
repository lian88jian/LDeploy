package per.lian.deploy.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.SocketException;

import per.lian.deploy.pojo.SocketData;
import per.lian.deploy.pojo.SocketDataType;
import per.lian.utils.FileUtil;

public class ClientReadThread extends Thread implements SocketDataType {

	private static ClientReadThread instance;
	private ObjectInputStream socketIn;
	private CommandThread commandThread;

	public static ClientReadThread getInstance() {
		if (instance == null) {
			instance = new ClientReadThread();
		}
		return instance;
	}

	private ClientReadThread() {

		commandThread = CommandThread.getInstance();
	}

	public void init(InputStream in) throws Exception {
		this.socketIn = new ObjectInputStream(in);
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

	private void _handleServerData(SocketData socketData) {

		switch (socketData.getType()) {
		case SERVER_CMD:
			commandThread.execute(socketData.getStringData());
			break;
		case SERVER_FILE:
			FileUtil.createFileWithBytes("F:\\temps\\tmp\\" + socketData.getMsg_2(), socketData.getMsg_1(), socketData.getData());
			break;
		}
	}
}

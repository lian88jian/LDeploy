package per.lian.deploy.pojo;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSONObject;

import per.lian.utils.DateUtil;
import per.lian.utils.FileUtil;

public class SocketData implements SocketDataType, Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final String CHARSET = "utf-8";
	
	private int type;
	private String msg_1;
	private String msg_2;
	private String msg_3;
	private String msg_4;
	private byte[] data;

	public SocketData(int type, byte[] data) {
		
		setType(type);
		setData(data);
	}
	
	public String getStringData(){
		try {
			return new String(this.data, CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static SocketData CLIENT_CMD_HEART(){
		return new SocketData(CLIENT_CMD_HEART, getByte(DateUtil.getDateYMDHMS()));
	}
	
	public static SocketData SERVER_FILE(File file, String path) {
		
		SocketData socketData = new SocketData(SERVER_FILE, FileUtil.getBytes(file));
		socketData.setMsg_1(file.getName());
		socketData.setMsg_2(path);
		return socketData;
	}

	public static SocketData CLIENT_CMD_MSG(String msg) {
		
		return new SocketData(CLIENT_CMD_MSG, getByte(msg));
	}

	public static SocketData SERVER_CMD(String cmd) {
		
		return new SocketData(SERVER_CMD, getByte(cmd));
	}
	
	private static byte[] getByte(String str){
		
		try {
			return str.getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SocketData CLIENT_INFO() {
		
		JSONObject clientInfo = new JSONObject();
		clientInfo.put("osname", System.getProperty("os.name"));
		return new SocketData(CLIENT_INFO, getByte(clientInfo.toJSONString()));
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public String getMsg_1() {
		return msg_1;
	}

	public void setMsg_1(String msg_1) {
		this.msg_1 = msg_1;
	}

	public String getMsg_2() {
		return msg_2;
	}

	public void setMsg_2(String msg_2) {
		this.msg_2 = msg_2;
	}

	public String getMsg_3() {
		return msg_3;
	}

	public void setMsg_3(String msg_3) {
		this.msg_3 = msg_3;
	}

	public String getMsg_4() {
		return msg_4;
	}

	public void setMsg_4(String msg_4) {
		this.msg_4 = msg_4;
	}
}

package per.lian.deploy.pojo;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.alibaba.fastjson.JSONObject;

import per.lian.utils.DateUtil;

public class SocketData implements SocketDataType,Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final String CHARSET = "utf-8";
	
	private int type;
	
	private byte[] data;

	public SocketData(int type, byte[] data) {
		
		setType(type);
		setData(data);
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
}

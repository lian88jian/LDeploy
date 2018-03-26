package per.lian.deploy.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import per.lian.deploy.pojo.SocketData;
import per.lian.deploy.pojo.SocketDataType;
import per.lian.utils.FileUtil;

public class ClientFileManager implements SocketDataType {
	
	/**
	 * 校验项目文件, 如果没有md5文件, 则先去请求
	 * @param flowName
	 * @param clientVersion
	 * @return
	 * @throws Exception
	 */
	public static boolean checkProjectFiles(String flowName, String clientVersion) throws Exception{
		
		//版本目录 E:\temp\temps\client\20180326
		String versionDirPath = SocketClient.WorkDir + "/" + clientVersion;
		String md5FilePath = versionDirPath + "/md5.txt";
		
		File versionDir = new File(versionDirPath);
		File md5File = new File(md5FilePath);
		
		if(!versionDir.exists()) {
			versionDir.mkdirs();
			System.out.println("prject version dir create success :" + versionDirPath);
		}
		
		if(!md5File.exists()) {
			
			//请求服务器md5文件, 第二个字段, 指定当前所做的流程
			//一键部署(FLOW_ONE_KEY_DEPLOY)
			SocketData socketData = new SocketData(CLIENT_REQUIRE_MD5, flowName, clientVersion);
			ClientReadThread.getInstance().sendSocketData(socketData);
			return false;
		}
		
		return validWithMd5File(flowName, clientVersion);
	}
	
	/**
	 * 根据md5文件, 校验项目文件列表
	 * @param flowName
	 * @param clientVersion
	 * @return
	 * @throws Exception
	 */
	public static boolean validWithMd5File(String flowName, String clientVersion) throws Exception {
		
		//版本目录 E:\temp\temps\client\20180326
		String versionDirPath = SocketClient.WorkDir + "/" + clientVersion;
		String md5FilePath = versionDirPath + "/md5.txt";
		
		File md5File = new File(md5FilePath);
		
		String md5FileContent = FileUtil.readFileContent(md5File, "utf-8");
		
		boolean result = true;
		JSONArray jsonArr = JSONArray.parseArray(md5FileContent);
		for(int i = 0; i < jsonArr.size(); i ++) {
			
			JSONObject fileMd5Json = jsonArr.getJSONObject(i);
			String filePath = fileMd5Json.getString("file");
			String md5 = fileMd5Json.getString("md5");
			
			File file = new File(versionDirPath + "/" + filePath);
			//校验文件
			if(!_validateFileMd5(file, md5)) {
				
				//请求服务器文件, 第二个字段, 指定当前所处的流程
				//一键部署(FLOW_ONE_KEY_DEPLOY)
				SocketData socketData = new SocketData(CLIENT_REQUIRE_FILE, flowName, clientVersion, filePath);
				ClientReadThread.getInstance().sendSocketData(socketData);
				result = false;
			}
		}
		return result;
	}
	/**
	 * 校验文件是否存在以及md5是否一致
	 * @param file
	 * @param md5
	 * @return
	 * @throws Exception
	 */
	private static boolean _validateFileMd5(File file, String md5) throws Exception {
		
		if(!file.exists()) {
			return false;
		}
		String _md5 = DigestUtils.md5DigestAsHex(new FileInputStream(file));
		return _md5.equals(md5);
	}
	
}

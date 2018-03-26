package per.lian.deploy.server;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import per.lian.utils.FileUtil;

public class ServerFileManager {

	/**
	 * 生成文件md5列表
	 */
	public static void generaterMd5File(String serverType, String serverVersion) throws Exception {

		String serverDirPath = SocketServer.WorkDir + "\\" + serverType + "\\" + serverVersion + "\\";
		File md5File = new File(serverDirPath + "md5.txt");
		if (md5File.exists()) {
			return;
		}
		
		JSONArray arr = new JSONArray();
		List<File> fileList = FileUtil.getFilesByExt(new File(serverDirPath), null);
		for (File _file : fileList) {
			
			String _md5 = DigestUtils.md5DigestAsHex(new FileInputStream(_file));
			JSONObject _md5Json = new JSONObject();
			_md5Json.put("file", _file.getAbsolutePath().replace(serverDirPath, ""));
			_md5Json.put("md5", _md5);
			arr.add(_md5Json);
		}
		FileUtil.createFileWithBytes(serverDirPath + "md5.txt", arr.toJSONString().getBytes("utf-8"));
	}
}

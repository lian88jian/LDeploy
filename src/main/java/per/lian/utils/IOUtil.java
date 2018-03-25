package per.lian.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {
	
	/**
	 * 关闭连接
	 * @author lian
	 * @date 2016年5月10日
	 * @param st
	 */
	public static void close(Closeable st){
		if( st == null ) return ;
		try {
			st.close();
			st = null;
		} catch (IOException e) {
		}
	}
}

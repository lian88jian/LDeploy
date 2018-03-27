package per.lian.web.core;

import com.alibaba.fastjson.JSONObject;

/**
 * json返回类
 * @author LianJian
 * @date 2014-12-26
 * @email lian00jian@163.com
 */
public class BaseResult {
	
	private boolean result;
	private String msg;
	private Object data;
	
	public BaseResult(boolean result){
		this.result = result;
		this.msg = "";
		this.data = "";
	}
	
	public BaseResult(boolean result,String msg){
		this.result = result;
		this.msg = msg==null ? "" : msg;
		this.data = "";
	}
	
	public BaseResult(boolean result,String msg,Object data){
		this.result = result;
		this.msg = msg==null ? "" : msg;
		this.data = data;
	}
	
	public BaseResult(int updateRow) {
		if(updateRow > 0){
			this.result = true;
		}else{
			this.result = false;
		}
		this.msg = "";
		this.data = "";
	}

	public static BaseResult getSuccess(){
		return new BaseResult(true);
	}
	public static BaseResult getFail(){
		return new BaseResult(false);
	}
	public static BaseResult getFailWithMsg(String msg){
		return new BaseResult(false,msg);
	}
	public static BaseResult getFailWithMsg(Throwable e){
		return new BaseResult(false,e.getMessage());
	}
	public static BaseResult getFailWithMsg(String msg,String data){
		return new BaseResult(false,msg,data);
	}
	
	public BaseResult setResult(boolean result) {
		this.result = result;
		return this;
	}
	public boolean getResult() {
		return result;
	}
	
	public boolean isSuccess(){
		return result;
	}
	
	public boolean isFail(){
		return !result;
	}
	public BaseResult setMsg(String msg) {
		this.msg = msg;
		return this;
	}
	public String getMsg() {
		return msg;
	}
	public BaseResult setData(Object data) {
		this.data = data;
		return this;
	}
	public Object getData() {
		return data;
	}
	
	public String toString(){
		return JSONObject.toJSONString(this);
	}
	
	/**
	 * 
	 * @author lian
	 * @date 2016年5月14日
	 * @param num 数据库操作行数
	 * @return 
	 */
	public static BaseResult getResult(int num) {
		if( num > 0 )
			return getSuccess();
		else
			return getFailWithMsg("数据库操作失败!");
	}
	
	/**
	 * 
	 * @author lian
	 * @date 2016年5月14日
	 * @param num 数据库操作行数
	 * @return 
	 */
	public static BaseResult getResult(int num,Object data) {
		if( num > 0 )
			return getSuccess().setData(data);
		else
			return getFailWithMsg("数据库操作失败!");
	}

	public static BaseResult getSuccess(Object object) {
		
		return getSuccess().setData(object);
	}

	public static BaseResult getResult(JSONObject result) {
		
		String errorMsg = null;
		if(result.containsKey("errorMsg")){
			errorMsg = result.get("errorMsg").toString();
		}
		Object data = null;
		if(result.containsKey("data")){
			data = result.get("data");
		}
		BaseResult br = new BaseResult(
				result.getBoolean("success"), 
				errorMsg, data);
		return br;
	}
}

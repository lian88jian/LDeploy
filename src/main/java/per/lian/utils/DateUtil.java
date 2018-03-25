package per.lian.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static Timestamp getTime() {
		Date now = new Date();
		return new Timestamp(now.getTime());
	}
	public static String getDateYMDHMS() {
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
		return dateFormat.format( now ); 
	}
	
	public static String getDateYMD() {
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
		return dateFormat.format( now ); 
	}
	
	public static String getDateFormat(String fmt){
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);//可以方便地修改日期格式
		return dateFormat.format( now ); 
	}
	
	public static String FormatYMDHMS(String inputDate) throws ParseException{
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		DateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH");
		DateFormat format4 = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = null;
		try{
			dt = format1.parse(inputDate);
		}catch(Exception e1){
			try{
				dt = format2.parse(inputDate);
			}catch(Exception e2){
				try{
					dt = format3.parse(inputDate);
				}catch(Exception e3){
					dt = format4.parse(inputDate);
				}
			}
		}
		return format1.format(dt);
	}
	
	public static String FormatYMD(String inputDate) throws ParseException{
//		Date date = new Date(inputDate);
//		if(inputDate.length() > 10)
//			inputDate = inputDate.substring(0, 10);
		 DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		 return format.format(format.parse(inputDate));
	}
}

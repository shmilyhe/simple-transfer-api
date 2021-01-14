package io.shmilyhe.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简单的数据处理类
 * @author eshore
 *
 */
public  class StringValue {

	
	
	public static int toInt(String str){
		if(str==null)return 0;
		if(str.indexOf('.')>-1){
			return (int)toDouble(str);
		}
		return Integer.parseInt(str);
	}
	
	public static long toLong(String str){
		if(str==null)return 0;
		return Long.parseLong(str);
	}
	
	public static float toFloat(String str){
		if(str==null)return 0;
		return Float.parseFloat(str);
	}
	
	public static String trim(String str){
		if(str==null)return null;
		return str.trim();
	}
	
	
	public static boolean toBoolean(String str ){
		if(str==null)return false;
		if("true".equals(trim(str).toLowerCase()))return true;
		if("false".equals(trim(str).toLowerCase()))return false;
		if(toDouble(str)>0)return true;
		return false;
	}
	
	public static double toDouble(String str){
		if(str==null)return 0;
		return Double.parseDouble(str);
	}
	
	
	private static Date _toDate(String y,String M,String d,String H ,String mm,String s){
		Calendar cal = Calendar.getInstance();
		cal.set(y == null ? 0 : Integer.parseInt(y), M == null ? 0
				: Integer.parseInt(M) - 1, d == null ? 0 : Integer
				.parseInt(d), H == null ? 0 : Integer.parseInt(H),
				mm == null ? 0 : Integer.parseInt(mm), s == null ? 0
						: Integer.parseInt(s));
		return cal.getTime();
	}
	
	/**
	 * 常用的日期格式转换
	 * @param dateString 日期字符串
	 * @return Date
	 */
	public static Date toDate(String dateString) {
		 Pattern p = Pattern.compile(" *(\\d{4})-(\\d{1,2})-(\\d{1,2}) *(\\d{1,2})?(:)?(\\d{1,2})?(:)?(\\d{1,2})?(\\.\\d*)? *");
		if (dateString == null)
			return null;
		Matcher m = p.matcher(dateString);
		if (m.matches()) {
			String y = m.group(1);
			String M = m.group(2);
			String d = m.group(3);
			String H = m.group(4);
			String mm = m.group(6);
			String s = m.group(8);
			return _toDate(y,M,d,H,mm,s);

		}
		Pattern p2 = Pattern.compile(" *(\\d{1,2})/(\\d{1,2})/(\\d{4}) *(\\d{1,2})?(:)?(\\d{1,2})?(:)?(\\d{1,2})?(\\.\\d*)? *");
		m = p2.matcher(dateString);
		if (m.matches()) {
			String y = m.group(3);
			String M = m.group(2);
			String d = m.group(1);
			String H = m.group(4);
			String mm = m.group(6);
			String s = m.group(8);
			return _toDate(y,M,d,H,mm,s);

		}
		Pattern p3 = Pattern.compile(" *(\\d{4})(\\d{2})(\\d{2}) *(\\d{1,2})?(:)?(\\d{1,2})?(:)?(\\d{1,2})?(\\.\\d*)? *");
		m = p3.matcher(dateString);
		if (m.matches()) {
			String y = m.group(1);
			String M = m.group(2);
			String d = m.group(3);
			String H = m.group(4);
			String mm = m.group(6);
			String s = m.group(8);
			return _toDate(y,M,d,H,mm,s);

		}
		Pattern p4 = Pattern.compile(" *(\\d{4})/(\\d{1,2})/(\\d{1,2}) *(\\d{1,2})?(:)?(\\d{1,2})?(:)?(\\d{1,2})?(\\.\\d*)? *");
		m = p4.matcher(dateString);
		if (m.matches()) {
			String y = m.group(1);
			String M = m.group(2);
			String d = m.group(3);
			String H = m.group(4);
			String mm = m.group(6);
			String s = m.group(8);
			return _toDate(y,M,d,H,mm,s);
		}
		
		return null;
	}
	public static String asString(InputStream in){
		if(in==null)return null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len=0;
		byte[] b= new byte[1024];
 		try {
			while((len=in.read(b))>0){
				bos.write(b, 0, len);
			}
			return new String(bos.toByteArray(),"utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 		return null;
		
	}
	
	public static void main(String agrs[]){
		
	}
}

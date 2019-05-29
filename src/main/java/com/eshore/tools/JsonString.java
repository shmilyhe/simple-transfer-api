package com.eshore.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class JsonString {
	static Log log =Logger.getLogger(JsonString.class);
	public static String asJsonString(Object o){
		StringBuilder json = new StringBuilder();
		asJson(json,o,0);
		return json.toString();
	}
	
	
	private static void asJson(StringBuilder json,Object o,int level){
		if(o==null){
			json.append("null");
		}else if(o instanceof String||o instanceof Boolean){
			json.append('"').append(o).append('"');
			return;
		}else if (o instanceof Integer
				||o instanceof Double
				||o instanceof Long
				||o instanceof Float
				||o instanceof Short
				||o instanceof Byte
				||o instanceof BigDecimal
				){
			json.append(o);
		}else if (o instanceof Date){
			json.append('"').append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(o)).append('"');
		}else if (o instanceof Array){
			jsonArray(json,(Object[])o,level);
		}else if (o instanceof Map){
			jsonMap(json,(Map)o,level);
		}else if (o instanceof Collection){
			jsonCollection(json,(Collection)o,level);
		}else{
			jsonObject(json,o,level);
		}
	}
	
	private static void jsonCollection(StringBuilder json,Collection co,int level){
		json.append('[');
		boolean isFirst=true;
		for(Object o:co){
			if(isFirst){isFirst=false;}else{json.append(',');}
			asJson(json,o,level+1);
		}
		json.append(']');
	}
	private static void jsonArray(StringBuilder json,Object[] co,int level){
		json.append('[');
		boolean isFirst=true;
		for(Object o:co){
			if(isFirst){isFirst=false;}else{json.append(',');}
			asJson(json,o,level+1);
		}
		json.append(']');
	}
	
	private static void jsonMap(StringBuilder json,Map map,int level){
		json.append('{');
		boolean isFirst=true;
		for(Object o:map.entrySet()){
			Map.Entry e=(Map.Entry)o;
			if(e.getValue()==null)continue;
			if(isFirst){isFirst=false;}else{json.append(',');}
			json.append('"').append(e.getKey()).append("\" : ");
			asJson(json,e.getValue(),level+1);
		}
		json.append('}');
	}
	
	private static String firstLower(String str){
		char [] ca=str.toCharArray();
		if(ca[0]<91)ca[0]+=32;
		return new String(ca);
	}
	private static void jsonObject(StringBuilder json,Object o,int level){
		json.append('{');
		boolean isFirst=true;
		Method [] methods = o.getClass().getDeclaredMethods();
		for(Method m:methods){
			String name =m.getName();
			if(!name.startsWith("get"))continue;
			Object value=null;
			try {
				value=m.invoke(o, null);
			} catch (Exception e) {
				e.printStackTrace();
				log.warm(" fail to get value:",o.getClass(),m.getName(), e);
			} 
			if(value==null)continue;
			name=firstLower(name.substring(3));
			
			if(isFirst){isFirst=false;}else{json.append(',');}
			json.append('"').append(name).append("\" : ");
			asJson(json,value,level+1);
		}
		json.append('}');
	}
	
	
	
	

}

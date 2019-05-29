package com.eshore.tools;

import java.lang.reflect.Field;

import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;

import java.lang.reflect.Type;

import java.math.BigDecimal;

import java.util.Calendar;

import java.util.Date;

import java.util.HashMap;

import java.util.Map;

import java.util.regex.Matcher;

import java.util.regex.Pattern;



//import javax.persistence.Column;

//import javax.persistence.EnumType;

//import javax.persistence.Enumerated;

public class Beans {

	//final Logger logger = LoggerFactory.getLogger(Beans.class);


	private static Map conver = new HashMap();


	private static Class javaxPersistenceColumn;
	private static Class  javaxPersistenceEnumType;
	private static Class javaxPersistenceEnumerated;
	private static boolean jpaApi=false;
	static{
		init();
	}
     public static void init(){
    	 try{
    		 javaxPersistenceColumn=Class.forName("javax.persistence.Column");
    		 javaxPersistenceEnumType=Class.forName("javax.persistence.EnumType");
    		 javaxPersistenceEnumerated=Class.forName("javax.persistence.Enumerated");
    		 jpaApi=true;
    	 }catch(Exception e){}
     }
	
 



	@SuppressWarnings("unchecked")
	public static <T> Object getBean(Map values, Class<T> clazz)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Object dest = clazz.newInstance();
		Map convers =getCoverMap(clazz);// (Map) conver.get(clazz);
		Method[] method = clazz.getMethods();
		/**
		 * 给实例填充
		 */

		for (int i = 0; i < method.length; i++) {
			Method meth = method[i];

			/**
			 * 当不是set的
			 */
			if (!meth.getName().startsWith("set"))
				continue;

			/**
			 * 当没有参数或参数个数大于1个时跳过
			 */

			// Type types[]= meth.getGenericParameterTypes();
			// if(types==null||types.length>1)continue;

			String name = meth.getName().substring(3).toUpperCase();
			field f = (field) convers.get(name);
			if (f == null)
				continue;
			if (f.dbName != null)
				name = f.dbName;
			Object param = f.conver(values.get(name));
			if (param == null)
				continue;
			meth.invoke(dest, param);
		}
		return dest;
	}
	
	public static Map toDBMap(Object o) throws Exception{
		Class<?> clazz=o.getClass();
		Method[] method = clazz.getMethods();
		Map cmap = getCoverMap(clazz);
		Map vmap = new HashMap();
		for (int i = 0; i < method.length; i++) {
			Method meth = method[i];
			if (!meth.getName().startsWith("get"))
				continue;
			if("getClass".equals(meth.getName()))continue;
			String name = meth.getName().toUpperCase().substring(3);
			
			field f = (field) cmap.get(name);
			if (f == null)
				continue;
			if (f.dbName != null)
				name = f.dbName;
			try{
			Object v=meth.invoke(o, null);
			vmap.put(name, v);
			}catch(Exception e){}
		}
		return vmap;
	}
	
	
	private static  Map getCoverMap(Class<?> clazz) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Map convers = (Map) conver.get(clazz);
		if (convers == null) {
			Method[] method = clazz.getMethods();
			convers = new HashMap();
			conver.put(clazz, convers);
			for (int i = 0; i < method.length; i++) {
				Method meth = method[i];
				if (!meth.getName().startsWith("get"))
					continue;
				boolean isColumn = false;
				if(jpaApi){
					isColumn=meth.isAnnotationPresent(javaxPersistenceColumn);
				}
				Type type = meth.getGenericReturnType();
				field f = new field();
				f.type = type;
				f.name = meth.getName().substring(3);
				f.dbName = f.name.toUpperCase();
				f.get=meth;
				if (isColumn) {


					Object column = meth.getAnnotation(javaxPersistenceColumn);
					Method nm=javaxPersistenceColumn.getMethod("name");
					String name =(String) nm.invoke(column); //column.name().toUpperCase();
					if(name!=null)name=name.toUpperCase();
					f.dbName = name;


				}

				if(jpaApi&&meth.isAnnotationPresent(javaxPersistenceEnumerated)){

					Object enumerated = meth.getAnnotation(javaxPersistenceEnumerated);
					Method nm=javaxPersistenceColumn.getMethod("value");
					String type1 =""+ nm.invoke(enumerated);
					//EnumType type1= enumerated.value();

					try {

						Method valueOf= ((Class)meth.getGenericReturnType()).getMethod("valueOf",new Class[]{String.class});
						Method vs= ((Class)meth.getGenericReturnType()).getMethod("values",new Class[]{});
						f.valueOf=valueOf;
						f.values=vs;
						f.isEnum=true;
						f.isEnumString=type1.toLowerCase().indexOf("string")>-1;//equals(EnumType.STRING);
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();

					}

				}
				convers.put((meth.getName().substring(3)).toUpperCase(), f);
			}

			Field [] fs = clazz.getDeclaredFields();
			if(fs!=null)
			for (Field f: fs) {
				String name = "set"+f.getName();
				field t = (field) convers.get(name.toUpperCase());
				if(t!=null){

					if(jpaApi&&f.isAnnotationPresent(javaxPersistenceColumn)){
					//Column column = f.getAnnotation(Column.class);
						Object column = f.getAnnotation(javaxPersistenceColumn);
						Method nm=javaxPersistenceColumn.getMethod("name");
						String dbname =(String) nm.invoke(column); //column.name().toUpperCase();
						if(dbname!=null)dbname=dbname.toUpperCase();
					//String dbname = column.name().toUpperCase();
					t.dbName = dbname;

					}

					if(jpaApi&&f.isAnnotationPresent(javaxPersistenceEnumerated)){

						/*
						Enumerated enumerated = f.getAnnotation(Enumerated.class);
						EnumType type= enumerated.value();
						 */
						Object enumerated =f.getAnnotation(javaxPersistenceEnumerated);
						Method nm=javaxPersistenceColumn.getMethod("value");
						String type1 =""+ nm.invoke(enumerated);
						try {

							Method valueOf= f.getType().getMethod("valueOf",new Class[]{String.class});
							Method vs= f.getType().getMethod("values",new Class[]{});
							t.valueOf=valueOf;
							t.values=vs;
							t.isEnum=true;
							t.isEnumString=type1.indexOf("string")>-1;//type.equals(EnumType.STRING);

							//enumTarget=
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();

						}

					}

				}

			}

		}
		return convers;
	}



}





/**
 * 转换的字
 * 
 * @author eric
 *
 */

class field {
	Type type;
	String dbName;
	String name;
	Method get;
	Method set;
	boolean isEnum=false;
	boolean isEnumString=true;
	Method valueOf;
	Method values;
	Object enumTarget;

	/**
	 * 把其它�?转换成目标类�?
	 * 
	 * @param o
	 * @return
	 */

	Object conver(Object o) {

		if (o == null)

			return null;

		Object dest = null;



		// 字符

		if(isEnum){
			if(isEnumString){
				String  st=o.toString();
				try {
					return valueOf.invoke(null,new Object[]{st});
				} catch (Exception e) {
					//e.printStackTrace();

					/**

					 * 支持 type/name 方式的 Enum

					 */

					try{
						Object[] vs = (Object[]) values.invoke(null, new Object[0]);
						for(Object en :vs){
							Object type = getField(en,"type");
							if(type!=null&&type.equals(st)){
								return en;
							}

						}

					}catch(Exception ex){}
					return null;

				}

			}else{
				Integer i =	(Integer) parseInt(o);
				if(i==null)return null;
			  try {
				Object[] vs = (Object[]) values.invoke(null, new Object[0]);
				if(vs!=null&&vs.length>i){
					return vs[i];
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

			  return null;

			}

		}else

		if (type == String.class) {
			if (o instanceof String) {
				dest = o;
			} else {
				dest = o.toString();
			}

		} else if (Integer.class.equals(type) || int.class.equals(type)) {
			dest = this.parseInt(o);
		} else if (Double.class.equals(type) || double.class.equals(type)) {
			dest = this.parseDouble(o);
		} else if (Short.class.equals(type) || short.class.equals(type)) {
			dest = this.parseShort(o);
		} else if (Float.class.equals(type) || float.class.equals(type)) {
			dest = this.parseFloat(o);
		} else if (Date.class.equals(type)) {
			dest = this.parseDate(o);
		} else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
			dest = this.parseBoolean(o);
		}else if(BigDecimal.class.equals(type)){
			return new BigDecimal(o.toString());

		}

		return dest;

	}



	private Object parseInt(Object o) {

		if (o == null)
			return null;

		if (o instanceof Integer) {
			return o;
		} else {

			try {
				double dv = Double.parseDouble(o.toString().trim());
				return (int) dv;
			} catch (Exception e) {
				return null;
			}

		}

	}



	private Object parseDouble(Object o) {
		if (o == null)
			return null;
		if (o instanceof Double) {
			return o;
		} else {

			try {
				return Double.parseDouble(o.toString().trim());
			} catch (Exception e) {
				return null;
			}

		}

	}



	private Object parseShort(Object o) {
		if (o == null)
			return null;
		if (o instanceof Short) {
			return o;

		} else {

			try {
				return (short) Double.parseDouble(o.toString().trim());
			} catch (Exception e) {

				return null;

			}

		}

	}



	@SuppressWarnings("unused")
	private Object parseLong(Object o) {
		if (o == null)
			return null;
		if (o instanceof Long) {
			return o;

		} else {
			try {
				return (long) Double.parseDouble(o.toString().trim());

			} catch (Exception e) {
				return null;
			}
		}
	}



	private Object parseBoolean(Object o) {
		if (o == null)
			return null;
		if (o instanceof Boolean) {
			return o;
		} else {

			try {
				return Double.parseDouble(o.toString().trim()) > 0;
			} catch (Exception e) {
				return null;

			}

		}

	}



	private Object parseFloat(Object o) {
		if (o == null)
			return null;
		if (o instanceof Float) {
			return o;
		} else {

			try {
				return (float) Double.parseDouble(o.toString().trim());
			} catch (Exception e) {
				return null;
			}

		}

	}



	private Object parseDate(Object o) {

		if (o == null)
			return null;
		if (o instanceof Date) {
			return o;
		} else {
			return converToDate(o.toString());

		}

	}


	public static Date converToDate(String dateString) {
		return StringValue.toDate(dateString);
	}

	public static Object getField(Object o,String name){

		try {
			Field f = getField1(o,name);
			if(f!=null){
				f.setAccessible(true);
				return f.get(o);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		return null;

	}

	

	private static Field getField1(Object object,String name){
		for(Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {    
            try {    
            	Field f = clazz.getDeclaredField(name) ;    
                return f ;    
            } catch (Exception e) {    
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。    
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了                 
            }    
        }
		return null;

	}

}


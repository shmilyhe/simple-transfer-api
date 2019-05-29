package com.eshore.tools;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * 数据包装类，json,yaml,xml数据读到内存中由包装类进行查找处理
 * @author eshore
 *
 */
public class SimpleDataWrap implements List{
	public static int TYPE_MAP=0; 
	public static int TYPE_ARRAY=1; 
	public static int TYPE_TEXT=2; 
	Map map;
	List array;
	String text;
	
	public void setData(Object o){
		if(o instanceof Map){
			setData((Map)o);
		}else if(o instanceof List){
			setData((List)o);
		}else {
			setData((String)o);
		}
	}
	
	public Set allKeys(){
		if(map==null)return new HashSet();
		return map.keySet();
	}
	
	public String getText(){
		return text;
	}
	
	public void setData(Map o){
			type=TYPE_MAP;
			map=o;
	}
	
	public void setData(List o){
		type=TYPE_ARRAY;
		array=o;
	}
	public void setData(String o){
		type=TYPE_TEXT;
		text=o;
	}
	
	private int type;
	public int getType(){
		return type;
	}
	
	public Object get(String k){
		if(map==null)return null;
		Object o =map.get(k);
		if(o instanceof String)return o;
		SimpleDataWrap sdw =new SimpleDataWrap();
		sdw.setData(o);
		return sdw;
	}
	
	public String getString(String k){
		Object o = find(k);
		if(!(o instanceof String)) return null;
		//if(map==null)return null;
		//return (String) map.get(k);
		return (String)o;
	}
	
	public int getInt(String k){
		return StringValue.toInt(getString(k));
	}
	
	public long getLong(String k){
		return StringValue.toLong(getString(k));
	}
	
	public float getFloat(String k){
		return StringValue.toFloat(getString(k));
	}
	
	public Date getDate(String k){
		return StringValue.toDate(getString(k));
	}
	
	public double getDouble(String k){
		return StringValue.toDouble(getString(k));
	}
	
	public boolean getBoolean(String k){
		return StringValue.toBoolean(getString(k));
	}

	@Override
	public int size() {
		return array==null? 0:array.size();
	}

	@Override
	public boolean isEmpty() {
		return array==null? false:array.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return array==null? false:array.contains(o);
	}

	@Override
	public Iterator iterator() {
		Iterator it =array==null?null:array.iterator();
		return new Iterator(){
			public boolean hasNext() {
				if(it==null)return false;
				return it.hasNext();
			}
			public Object next() {
				Object o=it.next();
				if(o instanceof String) return o;
				SimpleDataWrap sdw = new SimpleDataWrap();
				sdw.setData(o);
				return sdw;
			}
			
		};
	}

	@Override
	public Object[] toArray() {
		if(array==null)return null;
		Object[] ar = new Object[size()];
		int i=0;
		for(Object o:this){
			ar[i++]=o;
		}
		return ar;
	}

	@Override
	public Object[] toArray(Object[] a) {
		return  toArray();
	}

	@Override
	public boolean add(Object e) {
		if(array!=null){
		return	array.add(e);
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection c) {
		return array==null? false:array.containsAll(c);
	}

	@Override
	public boolean addAll(Collection c) {
		return false;
	}

	@Override
	public boolean addAll(int index, Collection c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection c) {
		return false;
	}

	@Override
	public void clear() {
		if(array!=null)array.clear();
	}

	@Override
	public Object get(int index) {
		if(array==null)return null;
		Object o =array.get(index);
		if(o instanceof String) return o;
		SimpleDataWrap sdw = new SimpleDataWrap();
		sdw.setData(o);
		return sdw;
	}

	@Override
	public Object set(int index, Object element) {
		return null;
	}

	@Override
	public void add(int index, Object element) {
		
	}

	@Override
	public Object remove(int index) {
		return array==null?null:array.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return array==null? -1:array.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return array==null? -1:array.lastIndexOf(o);
	}

	@Override
	public ListIterator listIterator() {
		return array==null? null:array.listIterator();
	}

	@Override
	public ListIterator listIterator(int index) {
		return array==null? null:array.listIterator(index);
	}

	@Override
	public List subList(int fromIndex, int toIndex) {
		return array==null? null:array.subList(fromIndex,toIndex);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		toString(sb,this);
		return sb.toString();
	}
	


	private void toString(StringBuilder sb,SimpleDataWrap sdw){
		if(sdw.getType()==TYPE_MAP){
			sb.append("{");
			Set<String> set =sdw.allKeys();
			boolean isFirst=true;
			for(String k:set){
				if(isFirst){
					isFirst=false;
				}else{
					sb.append(",");
				}
				sb.append("\"").append(k).append("\" : ");
				Object o=sdw.get(k);
				if(o instanceof SimpleDataWrap){
					toString(sb,(SimpleDataWrap)o);
					}else{
						sb.append("\"").append(o).append("\"");
					}
			}
			sb.append("}");
		}else if(sdw.getType()==TYPE_ARRAY){
			sb.append("[");
			boolean isFirst=true;
			for(Object o:sdw){
				if(isFirst){
					isFirst=false;
				}else{
					sb.append(",");
				}
				if(o instanceof SimpleDataWrap){
					toString(sb,(SimpleDataWrap)o);
					}else{
						sb.append("\"").append(o).append("\"");
					}
			}
			sb.append("]");
		}else{
			sb.append("\"").append(sdw.getText()).append("\"");
		}
	}
	
	/**
	 * 按路径查找值
	 * 按JSON 的语法查找如：
	 * 给定如下的数据[{personList:[1,2,3,4]}]
	 * find("[0].personList[3]") 的结果是4 
	 * 
	 * @param path 路径
	 * @return 查找结果(String||SimpleDataWrap)
	 */
	public Object find(String path){
		path=path.replaceAll("\\] *\\[", "].[");
		String [] paths =path.split("\\.");
		Object o=null;
		SimpleDataWrap flag =this;
		for(String p:paths){
			if(flag==null)return null;
			String k=p;
			int index=-1;
			if(k.indexOf('[')>-1){
				k=p.substring(0, p.indexOf('['));
				try{
				index=Integer.parseInt(p.substring(p.indexOf('[')+1, p.lastIndexOf(']')));
				}catch(Exception e){
					return null;
				}
			}
			if(flag.getType()==TYPE_MAP){
				 o=flag.get(k);
				 if(index>-1){
					 if(!(o instanceof SimpleDataWrap))return null;
					 SimpleDataWrap array =(SimpleDataWrap)o;
					 o=array.get(index);
				 }
			}else if(flag.getType()==TYPE_ARRAY){
				 o=flag.get(index);
			}
			
			 if(o instanceof SimpleDataWrap){
				 flag=(SimpleDataWrap)o;
			 }else{
				 flag=null;
			 }
		}
		return o;
	}
	
	
}

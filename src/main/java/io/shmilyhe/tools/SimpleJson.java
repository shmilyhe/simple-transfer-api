package io.shmilyhe.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 一个高效的简单的json 解析工具
 * 整个解析实现在200行代码内
 * @author eshore
 *
 */
public class SimpleJson {
	Object lv[]=new Object [10];//层级的值
	String lk[]=new String [10];//层级名称
	int lt[]=new int [10];//层级类型
	int maxDeep=10;
	
	public SimpleJson(){}
	
	public static SimpleJson parse(String str){
		SimpleJson sj = new SimpleJson();
		sj.fromString(str);
		return sj;
	}
	
	/**
	 * 
	 * @param deep 支持最大的层级，默认是10
	 */
	public SimpleJson(int deep){
		 lv =new Object [deep];//层级的值
		 lk =new String [deep];//层级名称
		 lt =new int [deep];//层级类型
		 maxDeep=deep;
	}
	
	/**
	 * 获取根
	 * @return root
	 */
	public Object getRoot(){
		return lv[0];
	}
	
	/**
	 * 从字符串解析JSON
	 * @param str text
	 */
  public	void fromString(String str){
		int level=0;
		int flag=0;
		String name=null;
		while(flag<str.length()){
			flag+=readBlank(str, flag);
			char c =str.charAt(flag);
			if(c=='{'){
				Map m =new HashMap();
				add(level,m,name);
				name=null;
				level++;
				flag++;
			}else if(c=='['){
				ArrayList list =new ArrayList();
				add(level,list,name);
				name=null;
				level++;
				flag++;
			}else if(c==']'||c=='}'){
				level--;
				flag++;
			}else if(c==','){
				flag++;
			}else if(c==':'){
				flag++;
			}else if(c=='\r'||c=='\n'){
				flag++;
			}else{//同级的
				flag+=readBlank(str, flag);
				String  text = readString(str, flag);
				flag+=tmp_read_len;
				if(lt[level-1]==0){//JSONOBJECT
					if(name==null){
						name=text;
					}else{
						add(level,text,name);
						name=null;
					}
				}else{//JSONARRAY
					name=null;
					add(level,text,null);
				}
			}
		}
	}
	
  /**
	 * 添加Object值
	 * @param level leval
	 * @param obj value
	 * @param name key
	 */
	private void add(int level,Map obj,String name){
		lv[level]=obj;
		lk[level]=name;
		lt[level]=0;
		if(level>0){
			if(lt[level-1]==0){
				((Map)lv[level-1]).put(name, obj);
			}else{
				((List)lv[level-1]).add(obj);
			}
		}
	}
	
	/**
	 * 添加Array值
	 * @param level leval
	 * @param obj value
	 * @param name key
	 */
	private void add(int level,List obj,String name){
		lv[level]=obj;
		lk[level]=name;
		lt[level]=1;
		if(level>0){
			if(lt[level-1]==0){
				((Map)lv[level-1]).put(name, obj);
			}else{
				((List)lv[level-1]).add(obj);
			}
		}
	}
	
	/**
	 * 添加字符值
	 * @param level leval
	 * @param obj value
	 * @param name key
	 */
	private void add(int level,String obj,String name){
		if(level>0){
			if(lt[level-1]==0){
				((Map)lv[level-1]).put(name, obj);
			}else{
				((List)lv[level-1]).add(obj);
			}
		}
	}
	
	

	/**
	 * 读到的长度
	 */
	private int tmp_read_len=0;
	/**
	 * 读一段字符串到stop char 时停止
	 * 以"或' 开头的读到配对的"或'.否则就以空格或逗号]}结束
	 * @param str source string
	 * @param off offset 
	 * @return value
	 */
	private String readString(String str,int off){
		tmp_read_len=0;
		int i=off;
		char stop=' ';
		char firstChar=str.charAt(off);
		if(firstChar=='"'||firstChar=='\''){
			stop=firstChar;
			off=i=i+1;
		}
		char lc=0;//上一个字符
		for(;i<str.length();i++){
			char c =str.charAt(i);
			if(stop==c&&lc!='\\')break;
			if(stop==' '&&(c==','||c==':'||c==']'||c=='}'))break;
			lc=c;
		}
		String value=str.substring(off,i);
		tmp_read_len=i-off;
		if(stop!=' ')tmp_read_len+=2;//没有引号的情况下算读取长度要加上两个引号
		return value;
	}
	
	
	/**
	 * 读完连续空格
	 * @param str
	 * @param off
	 * @return count_of_blank 
	 */
	private int readBlank(String str,int off){
		int i=off;
		for(;i<str.length();i++){
			char c =str.charAt(i);
			if(c!=' ')break;
		}
		return i-off;
	}	
	
	/**
	 * 扩展层级
	 * @param level 扩展支持的最深层级
	 */
	public void extendLevel(int level){
		if(maxDeep>level)return;
		int newDeep=maxDeep*2;
		Object lv_[]=new Object [newDeep];
		int lt_[]=new int [newDeep];
		String lk_[]=new String [newDeep];
		System.arraycopy(lv, 0, lv_, 0, lv.length);
		System.arraycopy(lt, 0, lt_, 0, lt.length);
		System.arraycopy(lk, 0, lk_, 0, lk.length);
		lv=lv_;
		lk=lk_;
		lt=lt_;
		maxDeep=newDeep;
	}
	
}

package com.eshore.socketapi.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eshore.socketapi.commons.Action;
/**
 * 保存订阅关系的池
 * @author eshore
 *
 */
public class CallBackPool {
	public static Map<String,List> workerMap = new HashMap<String,List> ();
	
	/**
	 * 调用客户端
	 * @param id 客户端订阅的id
	 * @param a 调用内容
	 */
	public static void call(String id, Action a){
		List list =workerMap.get(id);
		if(list==null||list.size()==0)return;
		for(Object o:list){
			ClientWorker w =(ClientWorker)o;
			try{
				if(w.available){
					w.Call(a);
				}
			}catch(Exception e){}
		}
	}
	
	/**
	 *  订阅
	 * @param id 订阅id
	 * @param worker 客户端的引用
	 */
	public  static void subscribe(String id,ClientWorker worker){
		List list = workerMap.get(id);
		if(list==null){
			list=new ArrayList();
			workerMap.put(id, list);
		}
		list.add(worker);
	}
}

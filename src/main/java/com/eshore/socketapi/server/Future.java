package com.eshore.socketapi.server;

import java.util.concurrent.TimeUnit;


import com.eshore.khala.utils.LRUCache;
import com.eshore.rpc.RPCFuture;
import com.eshore.socketapi.commons.Action;



public class Future {
	static LRUCache<String,RPCFuture> future = new LRUCache<String,RPCFuture>();
	public static RPCFuture submit(Action a){
		if(a.getId()==null)return null;
		RPCFuture rpc= new RPCFuture(a);
		future.put(a.getId(), rpc);
		return rpc;
	}
	
	public static void  done(Action a){
		if(a.getId()==null)return ;
		RPCFuture rpc=future.get(a.getId());
		rpc.done(a);
	}
	
	public static Action  get(String id,long timeout){
		RPCFuture rpc=future.get(id);
		if(rpc==null)return null;
		try {
			return (Action) rpc.get(3,TimeUnit.SECONDS);
		} catch (Exception e) {
			return null;
		}
	}
}

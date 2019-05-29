package com.eshore.httppass;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.eshore.socketapi.client.ICallback;
import com.eshore.socketapi.commons.Action;
import com.eshore.tools.HTTP;
import com.eshore.tools.SimpleJson;
import com.eshore.tools.StringValue;

public class HttpPassClient {
	static ThreadPoolExecutor queue = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
	String url;
	ICallback callback;
	public HttpPassClient(String url,ICallback callback){
		this.callback=callback;
		this.url=url;
		start();
	}
	
	private Action json2Action(SimpleJson json){
		Action a =new Action();
		Map m = (Map) json.getRoot();
		a.setAction((String) m.get("action"));
		a.setId((String) m.get("id"));
		m.remove("id");
		m.remove("action");
		a.setExt(m);
		return a;
	}
	
	private String action2String(Action a){
			if(a==null)return null;
			return a.toString();
	}
	
	private Action read(){
		try {
			String str =  StringValue.asString(HTTP.getInstance().doGet(url, null, true, null).getContent());
			SimpleJson json =	SimpleJson.parse(str);
			Action a = json2Action(json);
			return a;
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void  send(Action a){
		try {
			StringValue.asString(HTTP.getInstance().doPost(url, action2String(a)).getContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void start(){
		Thread th = new Thread(){
			public void run(){
				while(true){
					Action a = read();
					if(a!=null)
					queue.execute(new Runnable() {
						public void run() {
						 Action b =callback.doCallback(a);
						 if(b!=null){
							 send(b);
						 }
						}
					});
				}
			}
			
		};
		th.setDaemon(true);
		th.start();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		java.util.concurrent.Executors.newFixedThreadPool(20);
	}

}

package com.eshore.socketapi.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.eshore.socketapi.commons.Action;
import com.eshore.socketapi.commons.IProtocol;
import com.eshore.socketapi.commons.SimpleProtocol;

public class Client {
	IProtocol p;
	private String ip;
	private int port;
	public Client(String ip,int port,IProtocol p) throws UnknownHostException, IOException{
		this.p=p;
		s= new Socket(ip,port);
		this.ip=ip;
		this.port=port;
		out = s.getOutputStream();
		in =s.getInputStream();
		
	}
	
	private Socket s;
	private Socket callback;
	InputStream in;
	OutputStream out;
	
	InputStream callbackIn;
	OutputStream callbackOut;
	
	
	
	public boolean logon(String token){
		Action a= new Action();
		a.setAction("logon");
		a.setToken(token);
		Action b=  invoke(a);
		return "logon".equals(b.getAction());
	}
	
	ICallback handle;
	
	private long last=System.currentTimeMillis();
	private void testSubscribe(){
		if(System.currentTimeMillis()-last<1500){
			return;
		}
		last=System.currentTimeMillis();
		try{
			Action a= new Action();
			a.setAction("test");
			a.setToken("test");
			a.addAttribute("wx_id", id);
			p.write(callbackOut, a);
		}catch(Exception e){
			System.out.println("重连");
			Action a= new Action();
			a.setAction("subscribe");
			a.setToken("test");
			a.addAttribute("wx_id", id);
			try {
				
				callback= new Socket(ip,port);
				callbackIn=callback.getInputStream();
				callbackOut=callback.getOutputStream();
				p.write(callbackOut, a);
				caller=new DefaultCaller(p,callbackOut);
				System.out.println("重连成功");
			} catch (IOException e1) {
				System.out.println("重连失败");
				e1.printStackTrace();
			}
			
		}
	}
	Caller caller;
	String id;
	public void subscribe(String id,String token,final ICallback handle) throws UnknownHostException, IOException{
		callback= new Socket(ip,port);
		callbackIn=callback.getInputStream();
		callbackOut=callback.getOutputStream();
		this.id=id;
		caller=new DefaultCaller(p,callbackOut);
		Action a= new Action();
		a.setAction("subscribe");
		a.setToken(token);
		a.addAttribute("wx_id", id);
		p.write(callbackOut, a);
		this.handle=handle;
		System.out.println("连接成功");
		Thread th = new Thread(){
			public void run(){
				long count=0;
				while(true){
					try {
						if(count++%100==0){
							System.out.println(count-1);
						}
						if(callbackIn.available()>0){
							Action a = p.read(callbackIn);
							try{
								Action response = handle.doCallback(a);
								if(response!=null){
									response.setId(a.getId());
									caller.Call(response);
								}
							}catch(Exception e){}
						}else{
							
							try {
								testSubscribe();
								Thread.sleep(50);
								
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
				}
				System.out.println("exit");
			}
			
		};
		th.setDaemon(true);
		th.start();
	}
	
	
	public Action invoke(Action a){
		try {
			p.write(out, a);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	    return p.read(in);
	}
	
	
	public static void main(String agrs[]) throws UnknownHostException, IOException{
		{
			Client c = new Client("127.0.0.1",3000,new SimpleProtocol());
			Client c2 = new Client("127.0.0.1",3000,new SimpleProtocol());
			Client c3 = new Client("127.0.0.1",3000,new SimpleProtocol());
			Client c4 = new Client("127.0.0.1",3000,new SimpleProtocol());
			long bt = System.currentTimeMillis();
			c.logon("9090ooooooooooooo0");
			
			c.logon("9090ooooooo090909oooooo0");
			for(int i=0;i<10000;i++){
				c.logon("==================");
				c2.logon("00000000000000000000");
				c3.logon("00000000000000000000");
				c4.logon("00000000000000000000");
			}
			long time=System.currentTimeMillis()-bt;
			double tps=(40000d/time)*1000;
			System.out.println("time:"+time+"\t tps:"+tps);
		}
		{
			Client c = new Client("127.0.0.1",3000,new SimpleProtocol());
			Client c2 = new Client("127.0.0.1",3000,new SimpleProtocol());
			Client c3 = new Client("127.0.0.1",3000,new SimpleProtocol());
			Client c4 = new Client("127.0.0.1",3000,new SimpleProtocol());
			Client c5 = new Client("127.0.0.1",3000,new SimpleProtocol());
			Client c6 = new Client("127.0.0.1",3000,new SimpleProtocol());
			Client c7 = new Client("127.0.0.1",3000,new SimpleProtocol());
			Client c8 = new Client("127.0.0.1",3000,new SimpleProtocol());
			long bt = System.currentTimeMillis();
			c.logon("9090ooooooooooooo0");
			
			c.logon("9090ooooooo090909oooooo0");
			for(int i=0;i<10000;i++){
				c.logon("==================");
				c2.logon("00000000000000000000");
				c3.logon("00000000000000000000");
				c4.logon("00000000000000000000");
				c5.logon("==================");
				c6.logon("00000000000000000000");
				c7.logon("00000000000000000000");
				c8.logon("00000000000000000000");
			}
			long time=System.currentTimeMillis()-bt;
			double tps=(80000d/time)*1000;
			System.out.println("time:"+time+"\t tps:"+tps);
		}
		
	}
}

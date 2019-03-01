package com.eshore.socketapi.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.eshore.socketapi.commons.Action;
import com.eshore.socketapi.commons.IProtocol;
/**
 * 每个一个连接对应一个这样的独立worker
 * @author eshore
 *
 */
public class ClientWorker {
	Server server;
	String ip;
	InputStream in;
	OutputStream out;
	Socket s;
	int port;
	boolean available=true;
	boolean working;
	IProtocol protocol;
	ServerHandler serverHandler;
	/**
	 * 创建一个worker
	 * @param s 对端的socket
	 * @param h 业务处理handler
	 * @param protocol 传输协议
	 * @param server server 的反引用
	 */
	public ClientWorker(Socket s,ServerHandler h,IProtocol protocol,Server server){
		this.server=server;
		this.s=s;
		this.protocol=protocol;
		serverHandler=h;
		ip=s.getInetAddress().getHostAddress();
		port=s.getPort();
		System.out.println("accepet:"+ip+":"+port);
		try {
			in=s.getInputStream();
			out=s.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static Action ping=new Action("ping","ping",null);
	
	/**
	 * 当前worker 是否可用
	 * @return
	 */
	public boolean isAvailable() {
		if(s==null)return false;
		if(s.isClosed())return false;
		return available;
	}
	
	/**
	 * 关闭并释放资源
	 */
	public void close(){
		try {in.close();} catch (IOException e) {}
		try {out.close();} catch (IOException e) {}
		try {s.close();} catch (IOException e) {}
	}


	/**
	 * 是否处理在工作状态中
	 * @return
	 */
	public boolean isWorking() {
		return working;
	}


	/**
	 * 调用对端
	 * @param a
	 * @return
	 */
	public boolean Call(Action a){
		try {
			protocol.write(out, a);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			available=false;
			return false;
		}
		return true;
	}
	
	private final synchronized boolean toggleWork(boolean t){
		if(working==true){
			if(t==true)return false;
			else{
				working=false;
				return false;
			}
		}else{
			if(t==true){
				working=true;
				return true;
			}else{
				return false;
			}
		}
	}
	static long FREQ_OF_PING=10000;
	
	long lastPing=0;
	private boolean pingClient(){
		long t =System.currentTimeMillis();
		if(t-lastPing<FREQ_OF_PING)return true;
		try {
			protocol.write(out, ping);
			lastPing=t;
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * 处理接收的数据
	 * @return
	 */
	public  boolean  work(){
		if(!toggleWork(true))return false;
		//working=true;
		if(!available||s.isClosed()||!pingClient()){
			if(available==true)server.dropOneClient();
			available=false;
			toggleWork(false);
			//working=false;
			return false;
		}
		try {
			int av =in.available();
			if(av<=0){
				toggleWork(false);
				return false;
			}
			//System.out.println("work av:"+av);
			Action a =protocol.read(in);
			Action response =serverHandler.handle(a,this);
			if(response!=null)protocol.write(out, response);
			toggleWork(false);
			return true;
		} catch (IOException e) {
			available=false;
			e.printStackTrace();
			toggleWork(false);
			server.dropOneClient();
			return false;
		}
		
	}
}

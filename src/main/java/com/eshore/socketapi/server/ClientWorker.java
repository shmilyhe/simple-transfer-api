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
	 */
	public ClientWorker(Socket s,ServerHandler h,IProtocol protocol){
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
	
	/**
	 * 当前worker 是否可用
	 * @return
	 */
	public boolean isAvailable() {
		return available;
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
	
	/**
	 * 处理接收的数据
	 * @return
	 */
	public synchronized boolean  work(){
		working=true;
		if(!available||s.isClosed()){
			available=false;
			working=false;
			return false;
		}
		try {
			int av =in.available();
			if(av<=0){
				working=false;
				return false;
			}
			//System.out.println("work av:"+av);
			Action a =protocol.read(in);
			Action response =serverHandler.handle(a,this);
			if(response!=null)protocol.write(out, response);
			working=false;
			return true;
		} catch (IOException e) {
			available=false;
			e.printStackTrace();
			working=false;
			return false;
		}
		
	}
}

package com.eshore.socketapi.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.eshore.socketapi.commons.Action;
import com.eshore.socketapi.commons.IProtocol;

public class ClientWorker {
	String ip;
	InputStream in;
	OutputStream out;
	Socket s;
	int port;
	boolean available=true;
	IProtocol protocol;
	ServerHandler serverHandler;
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
	
	
	public synchronized boolean  work(){
		if(!available||s.isClosed()){
			available=false;
			return false;
		}
		try {
			int av =in.available();
			if(av<=0)return false;
			//System.out.println("work av:"+av);
			Action a =protocol.read(in);
			Action response =serverHandler.handle(a,this);
			if(response!=null)protocol.write(out, response);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			available=false;
			e.printStackTrace();
			return false;
		}
		
	}
}

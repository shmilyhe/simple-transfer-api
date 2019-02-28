package com.eshore.socketapi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.eshore.socketapi.commons.Action;
import com.eshore.socketapi.commons.IProtocol;
import com.eshore.socketapi.commons.SimpleProtocol;

/**
 * 服务端
 * @author eshore
 *
 */
public class Server {
	 ArrayList<ClientWorker> clientList= new ArrayList<ClientWorker>();
	 int loop=0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new Server(3000,new TestServerHandler(),new SimpleProtocol(),4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 通知客户端
	 * @param id
	 * @param a
	 */
	public void callClient(String id,Action a){
		CallBackPool.call(id, a);
	}
	
	/**
	 * 创建服务端
	 * @param port 监听端口
	 * @param hadler 客户端处理回调
	 * @param p 传输协议
	 * @param wokerSize 工作线程数
	 * @throws IOException
	 */
	public Server(int port,final ServerHandler hadler,final IProtocol p, int wokerSize) throws IOException{
		final ServerSocket s = new ServerSocket(port);
		Thread accepter = new Thread(){
			public void run(){
				while(true){
					Socket socket;
					try {
						socket = s.accept();
						clientList.add(new ClientWorker(socket,hadler,p));
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
		};
		accepter.setDaemon(true);
		accepter.start();
		final Server lock=this;
		for(int i=0;i<wokerSize;i++){
			Thread worker =	new Thread(){
				public void run(){
					int eCount=0;
					while(true){
						int loop=0;
						
						int size=clientList.size();
						//System.out.println("size:"+size);
						if(size==0)
							try {
								Thread.sleep(500);
								continue;
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						synchronized (lock) {
							loop=lock.loop;
							if(loop>=size){
								loop=0;
								lock.loop=0;
							}
							lock.loop++;
						}
						ClientWorker w =clientList.get(loop);
						if(!w.work())
							try {
								//System.out.println("sleep");
								eCount++;
								if(eCount>=50){
									Thread.sleep(1);
									eCount=0;
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}else{
								eCount=0;
							}
					}
				}
			};
			worker.setDaemon(true);
			worker.start();
		}
		
		
	}

}

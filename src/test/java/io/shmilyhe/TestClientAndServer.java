package io.shmilyhe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import org.junit.Test;

import io.shmilyhe.socketapi.client.Client;
import io.shmilyhe.socketapi.client.ICallback;
import io.shmilyhe.socketapi.commons.Action;
import io.shmilyhe.socketapi.commons.SimpleProtocol;
import io.shmilyhe.socketapi.server.Server;
import io.shmilyhe.socketapi.server.TestServerHandler;

public class TestClientAndServer {


	@Test
	public void test1(){
		 Server  server=null;
		/**
		 * 创建服务端
		 */
		try {
			 server =	new Server(3000,new TestServerHandler(),new SimpleProtocol(),4);
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
			Client c = new Client("127.0.0.1",3000,new SimpleProtocol());
			
			//订阅服务端数据
			c.subscribe("wx_id12345", "token", new ICallback(){

				@Override
				public Action doCallback(Action a) {
					try {
						System.out.println("接收到服务端的主动请求："+a.getAction()+" data:"+new String(a.getDatas(),"utf-8"));
						a.addAttribute("msg", "-------");
						return a;
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					return null;
					
				}
				
			});
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			{
				Action a = new Action();
				a.setAction("call from server");
				a.setDatas("test send data to client ".getBytes());
				Action c1= server.call("wx_id12345", a);
				System.out.println("客户端回复："+c1.getAttribute("msg"));
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			{
				Action a = new Action();
				a.setAction("queryUserList");
				a.addAttribute("name", "龙裕朝");
				Action resp = c.invoke(a);
				System.out.println("服务端返回："+a.getAttribute("name"));
			}
			
			{
				Action a = new Action();
				a.setAction("queryUserList2");
				a.addAttribute("name", "龙裕朝2");
				Action resp = c.invoke(a);
				System.out.println("服务端返回："+a.getAttribute("name"));
			}
			{
				Action a = new Action();
				a.setAction("queryUserList3");
				a.addAttribute("name", "龙裕朝3");
				Action resp = c.invoke(a);
				System.out.println("服务端返回："+a.getAttribute("name"));
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
}

package com.eshore;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import com.eshore.socketapi.client.Client;
import com.eshore.socketapi.client.ICallback;
import com.eshore.socketapi.commons.Action;
import com.eshore.socketapi.commons.SimpleProtocol;

public class Test2 {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Client c = new Client("70mm.cn",30016,new SimpleProtocol());
		
		//订阅服务端数据
		c.subscribe("1890", "token", new ICallback(){

			@Override
			public Action doCallback(Action a) {
				try {
					System.out.println("接收到服务端的主动请求："+a.getAction()+" data:"+new String(a.getDatas(),"utf-8"));
					a.addAttribute("msg", "我们收到了服务器的消息");
					return a;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return a;
			}
			
		});
		while(true)
		try {
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

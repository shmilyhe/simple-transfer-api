package com.eshore.socketapi.server;

import com.eshore.socketapi.commons.Action;

/**
 * 一个处理客户端的标准范例
 * 接入客服务端请求，并处理
 * @author eshore
 *
 */
public class TestServerHandler implements ServerHandler {

	@Override
	public Action handle(Action a,ClientWorker worker) {
		if("subscribe".equals(a.getAction())){
			String id =a.getAttribute("wx_id");
			CallBackPool.subscribe(id, worker);
			System.out.println("[Sever] 客户端订阅：id:"+id);
		}else {
			System.out.println("[Server] 接收到客服端请求："+a.getAction());
		}
		
		return a;
	}

}

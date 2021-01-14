package io.shmilyhe.httppass;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.shmilyhe.socketapi.commons.Action;
import io.shmilyhe.socketapi.server.Future;

public class RequestQueue {
	 static ArrayBlockingQueue<Action> arrayQueue = new ArrayBlockingQueue<Action>(5000);  //初始化一定要有长度
	
	 public static Action getResponse(Action action){
		 String id =UUID.randomUUID().toString();
		 action.setId(id);
		 try {
			boolean flag = arrayQueue.offer(action, 3, TimeUnit.SECONDS);
			Future.submit(action);
			return Future.get(id, 6);
		} catch (InterruptedException e) {
			action.setAction("timeOut");
			return action;
		} 
	 }
	 
	 public static Action getRequest(){
		 try {
			return arrayQueue.poll(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		 return null;
	 }
	 
	 public static void answer(Action a){
		 Future.done(a);
	 }
	 
	public static void main(String args[]) throws InterruptedException{
		ArrayBlockingQueue<String> arrayQueue = new ArrayBlockingQueue<String>(5000);  //初始化一定要有长度
		
		String str = arrayQueue.poll(2, TimeUnit.SECONDS);
		System.out.println(str);
		boolean flag = arrayQueue.offer("f", 2, TimeUnit.SECONDS);  //TimeUnit里面有时分秒等等，意思是多少时间后添加
		System.out.println(flag);

	}

}

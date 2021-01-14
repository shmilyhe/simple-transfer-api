package io.shmilyhe.socketapi.commons;

import java.io.DataOutputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.shmilyhe.rpc.RPCFuture;

public class Test {

	protected String a="123";
	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		// TODO Auto-generated method stub
		Scanner an;
		DataOutputStream dos ;//= new DataOutputStream();
		Action a = new Action();
		a.setId("123456");
		final RPCFuture fu=new RPCFuture(a);
		new Thread(){
			
			public void run(){
				try {
					Thread.sleep(10);
					fu.done(a);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		long bt =System.currentTimeMillis();
		Action b = (Action) fu.get(5, TimeUnit.SECONDS);
		System.out.println(System.currentTimeMillis()-bt);
		System.out.println("======="+b.getId());
		
	}

}

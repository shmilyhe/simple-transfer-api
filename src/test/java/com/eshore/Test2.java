package com.eshore;

import java.io.IOException;
import java.net.UnknownHostException;

import com.eshore.socketapi.client.Client;
import com.eshore.socketapi.commons.SimpleProtocol;

public class Test2 {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		Client c = new Client("127.0.0.1",3000,new SimpleProtocol());
		System.out.println( c.logon("9090ooooooooooooo0"));
		//System.out.println( c.logon("9090ooooooooooooo0"));
		//System.out.println( c.logon("9090ooooooooooooo0"));

	}

}

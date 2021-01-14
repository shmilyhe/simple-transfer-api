package io.shmilyhe.socketapi.client;

import java.io.IOException;
import java.io.OutputStream;

import io.shmilyhe.socketapi.commons.Action;
import io.shmilyhe.socketapi.commons.IProtocol;

public class DefaultCaller implements Caller {

	IProtocol p;
	OutputStream out;
	public DefaultCaller(IProtocol p,OutputStream out){
		this.p=p;
		this.out=out;
	}
	
	@Override
	public boolean Call(Action a) {
		// TODO Auto-generated method stub
		try {
			p.write(out, a);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}

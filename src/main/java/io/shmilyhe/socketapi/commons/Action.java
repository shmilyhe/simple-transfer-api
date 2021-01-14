package io.shmilyhe.socketapi.commons;

import java.util.HashMap;
import java.util.Map;

import io.shmilyhe.tools.JsonString;

/**
 * 事件类
 * @author eshore
 *
 */

public class Action {
	private String id;
	private String removeIp;//对端IP
	private String action;//事件名称
	private String token;//权限令牌，做权限认证时
	private byte datas[];//传输的数据
	private Map ext=new HashMap();//额外的属性
	
	/**
	 * 
	 */
	public Action(){}
	
	public Action(String action, String token,byte datas[]){
		this.action=action;
		this.token=token;
		this.datas=datas;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public byte[] getDatas() {
		return datas;
	}
	public void setDatas(byte[] datas) {
		this.datas = datas;
	}

	public Map getExt() {
		return ext;
	}

	public void setExt(Map ext) {
		this.ext = ext;
	}
	
	public String getAttribute(String name){
		return ""+this.ext.get(name);
	}
	
	public void addAttribute(String name,String v){
		this.ext.put(name, v);
	}

	public String getRemoveIp() {
		return removeIp;
	}

	public void setRemoveIp(String removeIp) {
		this.removeIp = removeIp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		if(ext==null)ext=new HashMap();
		ext.put("action", action);
		ext.put("id", id);
		if(token!=null){
			ext.put("token", token);
		}
		return JsonString.asJsonString(ext);
	}
	
	
}

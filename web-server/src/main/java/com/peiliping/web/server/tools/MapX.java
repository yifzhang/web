package com.peiliping.web.server.tools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class MapX extends HashMap<String,Object>{

	private static final long serialVersionUID = 1L;
	
	private volatile boolean lock = false  ;
	
	@Override
	@Deprecated
	public Object put(String key, Object value) {
		if(lock)  return null ;
		return super.put(key, value);
	}
	
	@Override
	@Deprecated
	public void putAll(Map<? extends String, ? extends Object> m) {
		if(lock) return ;
		super.putAll(m);
	}
	
	public MapX add(String l, Object r){
		if(lock)  return MapX.this ; 
		super.put(l, r);
		return MapX.this ;
	}
	
	public MapX add(Pair<String,Object> pair){
		if(lock) return MapX.this ;
		if(pair!=null){
			super.put(pair.getKey(), pair.getValue());
		}
		return MapX.this ;
	}
	
	public void LockModify(){
		lock = true ;
	}
	
	public static Map<String, Object> addAndGet(String l, Object r){
		return (new MapX()).add(l, r);
	}
}
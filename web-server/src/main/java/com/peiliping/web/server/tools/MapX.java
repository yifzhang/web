package com.peiliping.web.server.tools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;


public class MapX {
	
	private Map<String, Object> result = new HashMap<String, Object>();
	
	public MapX(){}
	
	public MapX(Map<String, Object> m){
		result = (m!=null ? m : result );
	}
	
	public MapX add(String l, Object r){
		result.put(l, r);
		return MapX.this ;
	}
	
	public MapX add(Pair<String,Object> pair){
		if(pair!=null){
			result.put(pair.getKey(), pair.getValue());
		}
		return MapX.this ;
	}
	
	public static Map<String, Object> addAndGet(String l, Object r){
		Map<String, Object> tmp = new HashMap<String, Object>();
		tmp.put(l, r);
		return tmp ;
	}
	
	public Map<String, Object> end(){
		return result ;
	}
}

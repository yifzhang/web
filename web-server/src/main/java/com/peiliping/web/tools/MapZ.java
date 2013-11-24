package com.peiliping.web.tools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class MapZ<T,P> {
	private Map<T, P> result = new HashMap<T, P>();
	
	public MapZ(){}
	
	public MapZ(Map<T, P> m){
		result = (m!=null ? m : result );
	}
	
	public MapZ<T,P> add(T t,P p){
		result.put(t, p);
		return MapZ.this ;
	}
	
	public MapZ<T,P> add(Pair<T,P> pair){
		if(pair!=null){
			result.put(pair.getKey(), pair.getValue());
		}
		return MapZ.this ;
	}
	
	public Map<T, P> addAndGet(T t , P p){
		result.put(t, p);
		return  result;
	}
	
	public Map<T,P> end(){
		return result ;
	}

}

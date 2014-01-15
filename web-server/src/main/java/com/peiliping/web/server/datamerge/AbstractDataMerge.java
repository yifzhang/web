package com.peiliping.web.server.datamerge;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

public abstract class AbstractDataMerge {
	
	protected String name ; 	
	protected Logger logger;
	
	protected boolean lru ;
	protected ConcurrentMap<String, AtomicReference<AbstractData>> MAP;
	protected int map_size = 1000 ;
	
	protected boolean needScheduleClean = false ;
	protected long scheduleTime ;
	
	protected long starttime = System.currentTimeMillis();	
	protected Timer timer ;
	
	public AbstractDataMerge (String name,Logger logger,int maxsize,boolean needScheduleClean,long scheduleTime,boolean lru){
		this.name = name ;
		this.logger = logger;
		this.map_size = maxsize;
		this.lru = lru ;
		this.MAP = lru ? new ConcurrentLinkedHashMap.Builder<String, AtomicReference<AbstractData>>().maximumWeightedCapacity(maxsize).weigher(Weighers.singleton()).build() 
				       : new ConcurrentHashMap<String, AtomicReference<AbstractData>>(map_size/4);
		this.needScheduleClean = needScheduleClean ;
		if(needScheduleClean){
			timer = new Timer() ;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					cleanData();
				}
			}, 0, scheduleTime);
		}
	}
	
	public void reset(){
		MAP.clear();
	}
	
	public int size (){
		return MAP.size();
	}
	
	public Set<String> dumpKey(){
		return MAP.keySet();
	}
	
	public boolean mergeData(AbstractData data){
		if(data==null) 
			return true ;
		AtomicReference<AbstractData> ref;
		while (true) {
			ref = MAP.get(data.getName());
			if (ref == null) {
				if(!lru && MAP.size()> map_size ) return false;
				if(lru && MAP.size()> map_size * 2 ) return false;
				ref = new AtomicReference<AbstractData>();
				if(MAP.putIfAbsent(data.getName(), ref) !=null ){
					continue;
				}				
			}
			if (ref.compareAndSet(ref.get(), data.merge(ref.get()))){
				return true;
			}
		}
	}
	
	protected abstract void cleanData();

//  e.g.
//	for(Entry<String, AtomicReference<AbstractData>> e : MAP.entrySet()){
//		if(e.getValue() != null && e.getValue().get() != null && e.getValue().get().getExpiretime() <  System.currentTimeMillis()){
//			MAP.remove(e.getKey());
//			e.getValue().get().cusumerAction() ;
//		}
//	}
	
}

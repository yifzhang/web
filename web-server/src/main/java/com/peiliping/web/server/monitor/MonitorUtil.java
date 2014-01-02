package com.peiliping.web.server.monitor;

import java.util.Map.Entry;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;

public class MonitorUtil implements IMonitorUtil {

	private ConcurrentMap<String, AtomicReference<MonitorResult>> monitorMap;

	private Timer timer ;
	
	private Logger logger;
	
	private int maxsize = 100 ;
	
	public MonitorUtil(Logger logger,int maxsize,boolean needScheduleLog,long scheduleTime,String... strings) {
		this.maxsize = maxsize ;
		this.logger = logger ;
		this.monitorMap = new ConcurrentHashMap<String, AtomicReference<MonitorResult>>(maxsize/4);
		if (strings != null && strings.length <= maxsize) {
			for (String item : strings) {
				monitorMap.put(item, new AtomicReference<MonitorResult>(new MonitorResult(0, 0, 0, 0, 0, 0))); 
			}
		}
		if (needScheduleLog) {
			timer = new Timer() ;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					toLog(true);
				}
			}, 0, scheduleTime);
		}
	}
	
	public MonitorUtil(Logger logger, int maxsize ,String... strings){
		this(logger,maxsize,true,2*60*1000,strings);
	}
	
	public MonitorUtil(int maxsize ,String... strings){
		this(null,maxsize,false,0,strings);
	}

	@Override
	public void reset() {
		monitorMap.clear();
	}

	@Override
	public MonitorResult getResult(String itemname) {
		AtomicReference<MonitorResult> ref = monitorMap.get(itemname);
		return ref==null ? null : ref.get();
	}
	
	@Override
	public long getActive(String itemname){
		MonitorResult mr = getResult(itemname);
		return mr == null ? 0 : mr.getActiveThread();
	}

	@Override
	public void in(String itemname, long q) {
		AtomicReference<MonitorResult> ref;
		while (true) {
			ref = monitorMap.get(itemname);
			if (ref == null) {
				if(monitorMap.size()> maxsize ) return ;
				ref = new AtomicReference<MonitorResult>(new MonitorResult(0, 0, 0, 0, 0, 0));
				if(monitorMap.putIfAbsent(itemname, ref) !=null ){
					continue;
				}
			}
			if (ref.compareAndSet(ref.get(), new MonitorResult(ref.get(), q, 0, 0, 0, 0, 0))) {
				return;
			}
		}
	}

	@Override
	public void in(String itemname) {
		in(itemname, 1);
	}

	@Override
	public void out(String itemname, long costtime, int type, long q) {
		AtomicReference<MonitorResult> ref;
		while (true) {
			ref = monitorMap.get(itemname);
			if (ref == null || ref.compareAndSet(ref.get(), new MonitorResult(ref.get(), q, costtime, type)))
				return;
		}
	}

	@Override
	public void out(String itemname, long costtime, int type) {
		out(itemname, costtime, type, 1);
	}

	@Override
	public void out(String itemname, long costtime) {
		out(itemname, costtime, MonitorResult.TYPE_SUCCESS);
	}

	@Override
	public void r2zero(String itemname) {
		AtomicReference<MonitorResult> ref ;
		while (true) {
			ref = monitorMap.get(itemname);
			if (ref == null	|| ref.compareAndSet(ref.get(),	new MonitorResult(ref.get())))
				return;
		}
	}

	@Override
	public void toLog(boolean needClean) {
		toLog(logger,needClean);
	}
	
	@Override
	public void toLog(Logger log , boolean needClean) {
		Iterator<Entry<String, AtomicReference<MonitorResult>>> it = monitorMap.entrySet().iterator();
		Entry<String, AtomicReference<MonitorResult>> e ;
		while(it.hasNext()){
			e = it.next();
			log.warn(e.getKey() + "\t" + e.getValue().get().tolog());
			if (needClean)
				r2zero(e.getKey());
		}
	}
}
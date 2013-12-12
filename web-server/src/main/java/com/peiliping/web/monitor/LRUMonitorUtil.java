package com.peiliping.web.monitor;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;

import com.peiliping.web.cache.LRU.LRUCache;

public class LRUMonitorUtil {

	private LRUCache monitorMap;

	private Timer timer ;
	
	private Logger logger;
	
	private int maxsize = 100 ;
	
	/**
	 * 把要统计的条目列出来
	 * 
	 * @param strings
	 */
	public LRUMonitorUtil(Logger logger,int maxsize,boolean needScheduleLog,long scheduleTime,String... strings) {
		this.maxsize = maxsize ;
		this.logger = logger ;
		this.monitorMap = new LRUCache("LRUMonitor", maxsize, Long.MAX_VALUE);
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
	
	public LRUMonitorUtil(Logger logger, int maxsize ,String... strings){
		this(logger,maxsize,true,2*60*1000,strings);
	}
	
	public LRUMonitorUtil(int maxsize ,String... strings){
		this(null,maxsize,false,0,strings);
	}

	public void reset() {
		monitorMap.clear();
	}

	/**
	 * 获得某条目的当前统计对象
	 * 
	 * @param itemname
	 * @return
	 */
	public MonitorResult getResult(String itemname) {
		AtomicReference<MonitorResult> ref = convert(monitorMap.get(itemname));
		return ref==null ? null : ref.get();
	}
	
	public long getActive(String itemname){
		MonitorResult mr = getResult(itemname);
		return mr == null ? 0 : mr.getActiveThread();
	}

	/**
	 * 进入的时候计数
	 * 
	 * @param itemname
	 *            计数条目名称
	 * @param q
	 *            默认写1
	 */
	public void in(String itemname, long q) {
		AtomicReference<MonitorResult> ref;
		while (true) {
			ref = convert(monitorMap.get(itemname));
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

	public void in(String itemname) {
		in(itemname, 1);
	}

	/**
	 * 退出的时候计数
	 * 
	 * @param itemname
	 *            条目名称
	 * @param costtime
	 *            消耗时间
	 * @param type
	 *            @link com.peiliping.web.monitor.MonitorResult
	 *            TYPE_SUCCESS TYPE_FAILURE TYPE_EXCEPTION
	 * @param q
	 *            默认写1
	 */
	public void out(String itemname, long costtime, int type, long q) {
		AtomicReference<MonitorResult> ref ;
		while (true) {
			ref = convert(monitorMap.get(itemname));
			if (ref == null || ref.compareAndSet(ref.get(), new MonitorResult(ref.get(), q, costtime, type)))
				return;
		}
	}

	public void out(String itemname, long costtime, int type) {
		out(itemname, costtime, type, 1);
	}

	public void out(String itemname, long costtime) {
		out(itemname, costtime, MonitorResult.TYPE_SUCCESS);
	}

	/**
	 * 清零
	 * 
	 * @param itemname
	 */
	public void r2zero(String itemname) {
		AtomicReference<MonitorResult> ref;
		while (true) {
			ref = convert(monitorMap.get(itemname));
			if (ref == null	|| ref.compareAndSet(ref.get(),	new MonitorResult(ref.get())))
				return;
		}
	}

	/**
	 * 打印日志
	 * 
	 * @param log
	 * @param needClean
	 *            打印后是否清零 建议清零
	 */
	public void toLog(boolean needClean) {
		toLog(logger,needClean);
	}
	
	public void toLog(Logger log , boolean needClean) {
		Iterator<Object> it = monitorMap.dumpKey().iterator();
		String key ;
		while(it.hasNext()){
			key = (String)it.next();
			log.warn(key + "\t" + convert(monitorMap.get(key)).get().tolog());
			if (needClean)
				r2zero(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static AtomicReference<MonitorResult> convert(Object o){
		return o == null ? null : (AtomicReference<MonitorResult>) o;
	}
}
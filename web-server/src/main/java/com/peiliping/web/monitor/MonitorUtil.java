package com.peiliping.web.monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;

public class MonitorUtil {

	private Map<String, AtomicReference<MonitorResult>> monitorMap;

	private Timer timer ;
	
	private Logger logger;
	
	/**
	 * 把要统计的条目列出来
	 * 
	 * @param strings
	 */
	public MonitorUtil(Logger logger,boolean needScheduleLog,long scheduleTime,String... strings) {
		this.logger = logger ;
		this.monitorMap = new HashMap<String, AtomicReference<MonitorResult>>();
		for (String item : strings) {
			AtomicReference<MonitorResult> ref = new AtomicReference<MonitorResult>(new MonitorResult(0, 0, 0, 0, 0, 0));
			monitorMap.put(item, ref);
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
	
	public MonitorUtil(Logger logger,String... strings){
		this(logger,true,2*60*1000,strings);
	}
	
	public MonitorUtil(String... strings){
		this(null,false,0,strings);
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
		AtomicReference<MonitorResult> ref = monitorMap.get(itemname);
		if (ref == null) {
			ref = new AtomicReference<MonitorResult>(new MonitorResult(0, 0, 0, 0, 0, 0));
		}
		return ref.get();
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
		while (true) {
			AtomicReference<MonitorResult> ref = monitorMap.get(itemname);
			if (ref == null) {
				ref = new AtomicReference<MonitorResult>(new MonitorResult(0, 0, 0, 0, 0, 0));
				monitorMap.put(itemname, ref);
			}
			MonitorResult oldr = ref.get();
			MonitorResult newr = new MonitorResult(ref.get(), q, 0, 0, 0, 0, 0);
			if (ref.compareAndSet(oldr, newr)) {
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
		while (true) {
			AtomicReference<MonitorResult> ref = monitorMap.get(itemname);
			if (ref == null) {
				ref = new AtomicReference<MonitorResult>(new MonitorResult(0, 0, 0, 0, 0, 0));
				monitorMap.put(itemname, ref);
			}
			MonitorResult oldr = ref.get();
			MonitorResult newr = new MonitorResult(ref.get(), q, costtime, type);
			if (ref.compareAndSet(oldr, newr)) {
				return;
			}
		}
	}

	public void out(String itemname, long costtime, int type) {
		out(itemname, costtime, type, 1);
	}

	public void out(String itemname, long costtime) {
		out(itemname, costtime, MonitorResult.TYPE_SUCCESS, 1);
	}

	/**
	 * 清零
	 * 
	 * @param itemname
	 */
	public void r2zero(String itemname) {
		while (true) {
			AtomicReference<MonitorResult> ref = monitorMap.get(itemname);
			if (ref == null) {
				ref = new AtomicReference<MonitorResult>(new MonitorResult(0, 0, 0, 0, 0, 0));
				monitorMap.put(itemname, ref);
				return;
			}
			MonitorResult oldr = ref.get();
			MonitorResult newr = new MonitorResult(oldr);
			if (ref.compareAndSet(oldr, newr)) {
				return;
			}
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
		for (Entry<String, AtomicReference<MonitorResult>> e : monitorMap.entrySet()) {
			logger.warn(e.getKey() + "\t" + e.getValue().get().tolog());
			if (needClean) {
				r2zero(e.getKey());
			}
		}
	}
	
	public void toLog(Logger log , boolean needClean) {
		for (Entry<String, AtomicReference<MonitorResult>> e : monitorMap.entrySet()) {
			log.warn(e.getKey() + "\t" + e.getValue().get().tolog());
			if (needClean) {
				r2zero(e.getKey());
			}
		}
	}
}
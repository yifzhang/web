package com.peiliping.web.monitor;

import org.slf4j.Logger;

public interface IMonitorUtil {
	
	/**
	 * 获得某条目的当前统计对象
	 * 
	 * @param itemname
	 * @return
	 */
	public MonitorResult getResult(String itemname) ;
	public long getActive(String itemname);
	public void reset() ;
	
	/**
	 * 进入的时候计数
	 * 
	 * @param itemname
	 *            计数条目名称
	 * @param q
	 *            默认写1
	 */
	public void in(String itemname, long q) ;
	public void in(String itemname) ;
	
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
	public void out(String itemname, long costtime, int type, long q) ;
	public void out(String itemname, long costtime, int type) ;
	public void out(String itemname, long costtime); 
	
	/**
	 * 清零
	 * 
	 * @param itemname
	 */
	public void r2zero(String itemname);
	
	/**
	 * 打印日志
	 * 
	 * @param log
	 * @param needClean
	 *            打印后是否清零 建议清零
	 */
	public void toLog(boolean needClean);
	public void toLog(Logger log , boolean needClean);
}
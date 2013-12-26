package com.peiliping.web.paialleltool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPaiallelTool<R> {
	
	public static final long TIME_OUT_DEFAULT = 500 ; 
	public static final long PRAY_TIME = 2 ;
	public static final int RUNNING_THREAD_NUM_DEFAULT = 3 ;
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected long timeout = TIME_OUT_DEFAULT;
	protected long praytime = PRAY_TIME;
	protected int maxsize = Integer.MAX_VALUE;
	protected int runningThreadNum  = RUNNING_THREAD_NUM_DEFAULT; 
	
	public AbstractPaiallelTool(long timeout,long praytime,int runningthreadnum,int maxsize ){
		this.timeout = timeout;
		this.praytime = praytime;
		this.runningThreadNum = runningthreadnum ;
		this.maxsize = maxsize ;
	}
	
	protected void checkParams(List<Callable<R>> callableList){
		if (callableList == null || callableList.size() == 0) {
			throw new IllegalArgumentException("params invalid");
		}
	}
	
	protected ExecutorService getPool(){
		return new ThreadPoolExecutor(runningThreadNum, maxsize,
                0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
	}
	
	public abstract PaiallelResult<R> run(List<Callable<R>> callableList) ;
	
	protected PaiallelResult<R> runcore(List<Callable<R>> callableList,ExecutorService pool){
		PaiallelResult<R> pr = new PaiallelResult<R>() ; 
		List<FutureTask<R>> l = new ArrayList<FutureTask<R>>();
		List<R> result = new ArrayList<R>();
		long remaintime = timeout;

		try {
			FutureTask<R> dbtask;
			for (int i = 0; i < callableList.size(); i++) {
				dbtask = new FutureTask<R>(callableList.get(i));
				l.add(dbtask);
				pool.submit(dbtask);
			}
			long start, end;
			R t = null;
			for (int i = 0; i < callableList.size(); i++) {
				dbtask = l.get(i);
				start = System.currentTimeMillis();
				if (remaintime > 0) {
					try {
						t = dbtask.get(remaintime, TimeUnit.MILLISECONDS);
					} catch (Exception e) {
						logger.error("PaiallerTool",e);
						pr.isComplete = false ;
					}
				}
				end = System.currentTimeMillis();
				if(t != null){
					result.add(t);
				}
				remaintime = remaintime - (end - start);
				if (remaintime <= 0 && i != callableList.size() -1 ) {
					remaintime = praytime;
				}
			}
		} catch (Exception e) {
			pr.isComplete = false ;
			logger.error("PaiallerTool",e);
		}
		pr.costtime = timeout - remaintime ;
		pr.resultList = result ;
		return pr;
	}
}
package com.peiliping.web.paialleltool;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class IndependentPoolPaiallelTool<R> extends AbstractPaiallelTool<R> {
	
	public IndependentPoolPaiallelTool(long timeout, long praytime,int runningthreadnum ,int maxszie) {
		super(timeout, praytime, runningthreadnum, maxszie);
	}

	@Override
	public PaiallelResult<R> run(List<Callable<R>> callableList) {
		checkParams(callableList);
		return runcore(callableList,getPool(callableList.size()));
	}
	
	private ExecutorService getPool(int size) {
		return new ThreadPoolExecutor(runningThreadNum, size,
                0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
	}
}
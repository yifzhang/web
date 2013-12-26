package com.peiliping.web.paialleltool;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class SharePoolPaiallelTool<R> extends AbstractPaiallelTool<R> {
	
	private ExecutorService sharePool;
	
	public SharePoolPaiallelTool(long timeout, long praytime,int runningthreadnum,int maxsize) {
		super(timeout, praytime, runningthreadnum,maxsize);
		this.sharePool = getPool();
	}

	@Override
	public PaiallelResult<R> run(List<Callable<R>> callableList) {
		checkParams(callableList);
		return runcore(callableList,sharePool);
	}
}
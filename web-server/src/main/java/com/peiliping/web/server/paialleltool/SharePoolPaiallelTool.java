package com.peiliping.web.server.paialleltool;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class SharePoolPaiallelTool<R> extends AbstractPaiallelTool<R> {
	
	private ExecutorService sharePool;
	
	public SharePoolPaiallelTool(long timeout, long praytime,int runningthreadnum,int maxsize) {
		super(timeout, praytime, runningthreadnum,maxsize);
		this.sharePool = getPool(null);
	}

	@Override
	public PaiallelResult<R> run(List<Callable<R>> callableList) {
		checkParams(callableList);
		return runcore(callableList,sharePool,null);
	}

	@Override
	public PaiallelResult<R> run(List<Callable<R>> callableList,Long tmp_timeout) {
		checkParams(callableList);
		return runcore(callableList,sharePool,tmp_timeout);
	}

	@Override
	public PaiallelResult<R> asyncRun(List<Callable<R>> callableList) {
		checkParams(callableList);
		return asyncRuncore(callableList, sharePool);
	}

}
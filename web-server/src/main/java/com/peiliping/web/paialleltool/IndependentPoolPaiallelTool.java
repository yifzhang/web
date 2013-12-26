package com.peiliping.web.paialleltool;

import java.util.List;
import java.util.concurrent.Callable;

public class IndependentPoolPaiallelTool<R> extends AbstractPaiallelTool<R> {
	
	public IndependentPoolPaiallelTool(long timeout, long praytime,int runningthreadnum ,int maxszie) {
		super(timeout, praytime, runningthreadnum, maxszie);
	}

	@Override
	public PaiallelResult<R> run(List<Callable<R>> callableList) {
		checkParams(callableList);
		return runcore(callableList,getPool(callableList.size()),null);
	}

	@Override
	public PaiallelResult<R> run(List<Callable<R>> callableList,Long tmp_timeout) {
		checkParams(callableList);
		return runcore(callableList,getPool(callableList.size()),tmp_timeout);
	}

}
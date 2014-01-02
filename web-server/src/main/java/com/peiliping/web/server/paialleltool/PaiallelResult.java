package com.peiliping.web.paialleltool;

import java.util.List;

public class PaiallelResult<R> {

	//并行执行结果
	public List<R> resultList ;
	
	//全部执行完成
	public boolean isComplete = true ;
	
	//全部完成消耗的时间
	public long costtime = 0 ;
	
}

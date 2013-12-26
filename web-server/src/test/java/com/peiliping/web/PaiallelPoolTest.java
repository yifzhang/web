package com.peiliping.web;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.peiliping.web.paialleltool.SharePoolPaiallelTool;

public class PaiallelPoolTest {

	public static void main(String[] args) {
		
		List<Callable<String>> lc = new ArrayList<Callable<String>>();
		lc.add(new Callable<String>(){
			@Override
			public String call() throws Exception {
				Thread.sleep(1000);
				return "ABC";
			}
		});
		SharePoolPaiallelTool<String> sp = new SharePoolPaiallelTool<String>(500, 2, 3, 10);
		sp.run(lc);

	}
}
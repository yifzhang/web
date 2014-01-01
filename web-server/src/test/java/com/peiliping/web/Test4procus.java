package com.peiliping.web;

import com.peiliping.web.proAcus.Consumers;
import com.peiliping.web.proAcus.IStorage;
import com.peiliping.web.proAcus.ListStorage;
import com.peiliping.web.proAcus.Producers;

public class Test4procus {

	public static void main(String[] args) throws InterruptedException {
		
		final IStorage<String> s = new ListStorage<String>(10);
		Consumers<String> cs = new Consumers<String>(10, s,10000) {
			@Override
			public ConsumerActionThread<String> getInstance() {
				return new ConsumerActionThread<String>(s, 1000) {
					@Override
					public void doConsume(String v) {
						System.out.println("DEL:"+v);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
				};
			}
		};
		cs.run(cs.getInstance());
		
		Producers<String> ps = new Producers<String>(10,s) {
			@Override
			public ProducerActionThread<String> getInstance() {
				return new ProducerActionThread<String>(s, 1000) {
					@Override
					public void doProduce() {
						getStorage().add("ABC");
						System.out.println("ADD:ABC" );
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
				};
			}
		};
		
		ps.run(ps.getInstance());
		ps.run(ps.getInstance());
		
		while(true){
			System.out.println("Storage" + s.size() + "Consumer" + cs.getPool().getActiveCount() + "Producer" + ps.getPool().getActiveCount());
			System.out.println("Storage" + s.size() + "Consumer" + cs.getThreads().size() + "Producer" + ps.getThreads().size());
			Thread.sleep(1000);
		}
		
	}
}
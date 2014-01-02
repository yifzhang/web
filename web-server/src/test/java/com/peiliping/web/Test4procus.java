package com.peiliping.web;

import com.peiliping.web.server.proAcus.Consumers;
import com.peiliping.web.server.proAcus.Consumers.ConsumerActionThread;
import com.peiliping.web.server.proAcus.IStorage;
import com.peiliping.web.server.proAcus.ListStorage;
import com.peiliping.web.server.proAcus.Producers;
import com.peiliping.web.server.proAcus.Producers.ProducerActionThread;

public class Test4procus {

	public static void main(String[] args) throws InterruptedException {
		
		final IStorage<String> s = new ListStorage<String>(1000);
		Consumers<String> cs = new Consumers<String>(10, s,3000) {
			@Override
			public ConsumerActionThread<String> getInstance() {
				return new ConsumerActionThread<String>(s, 500) {
					@Override
					public void doConsume(String v) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}
				};
			}
		};
		
		cs.setMinThreadsNum(6);
		cs.run(3);
		
		Producers<String> ps = new Producers<String>(10,s) {
			@Override
			public ProducerActionThread<String> getInstance() {
				return new ProducerActionThread<String>(s, 500) {
					@Override
					public void doProduce() {
						getStorage().add("ABC");
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
						}
					}
				};
			}
		};
		
		ps.run(4);
		
		while(true){
			System.out.println("Storage" + s.size() + "Consumer" + cs.getPool().getActiveCount() + "Producer" + ps.getPool().getActiveCount());
			System.out.println("Storage" + s.size() + "Consumer" + cs.getThreads().size() + "Producer" + ps.getThreads().size());
			Thread.sleep(1000);
		}
		
	}
}
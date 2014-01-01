package com.peiliping.web.proAcus;

import java.util.Timer;
import java.util.TimerTask;


public abstract class Consumers<V> extends AbstractRole<V> {

	protected Timer timer = new Timer();
	
	private  boolean prepareforremove = false ;
	
	public Consumers(int concurrencyThreadsNum, IStorage<V> storage,long checktime) {
		super(concurrencyThreadsNum ,storage);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(getStorage().is80()){
					addOne(getInstance());
				}
				
				if(getStorage().is3() && getThreads().size() > getMinThreadsNum()){
					if(prepareforremove){
						removeOne();
					}
					prepareforremove = !prepareforremove ;
					return ;
				}
				prepareforremove = false ;
			}
		}, checktime, checktime);
	}
	
	public abstract ConsumerActionThread<V> getInstance();

	
	public abstract class ConsumerActionThread<K> extends AbstractActionThread<K> {

		public ConsumerActionThread(IStorage<K> s,long sleeptime) {
			super(s,sleeptime);
		}

		@Override
		public void doAction() {
			while (!dropstatus) {
				K v = getStorage().del();
				if (v != null) {
					doConsume(v);
				}else{
					try {
						Thread.sleep(getSleeptime());
					} catch (InterruptedException e) {
					}
				}
			}
		}
		
		public abstract void doConsume(K v);
	} 
}

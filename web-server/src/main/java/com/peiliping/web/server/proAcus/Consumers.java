package com.peiliping.web.server.proAcus;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Consumers<V> extends AbstractRole<V> {

	protected Timer timer = new Timer();
	private  int prepareforremove = 0 ;
	private  final static int REMOVE_LEVEL = 2;
	
	public Consumers(int concurrencyThreadsNum, IStorage<V> storage,long checktime) {
		super(concurrencyThreadsNum ,storage);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(getStorage().isdayu(0.7)){
					addOne(getInstance());
				}else if(getStorage().isxiaoyu(0.01) && canRemoveOne()){
					if(prepareforremove >= REMOVE_LEVEL ){	removeOne(); }
					prepareforremove = prepareforremove >= REMOVE_LEVEL ? 0 : prepareforremove + 1;
					return ;
				}
				prepareforremove = 0 ;
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

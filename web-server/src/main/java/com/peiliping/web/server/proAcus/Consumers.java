package com.peiliping.web.server.proAcus;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Consumers<V> extends AbstractRole<V> {

	protected Timer timer = new Timer();
	private  int prepareforremove = 0 ;
	
	public volatile int REMOVE_LEVEL = 2;
	public volatile double ADD_LEVEL = 0.7;
	public volatile double DEL_LEVEL = 0.02;
	
	
	public Consumers(String name,int concurrencyThreadsNum, IStorage<V> storage,long checktime) {
		super(name,concurrencyThreadsNum ,storage);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.warn("ActiveThread:" + getPool().getActiveCount() + "\t" + "Storage:" + getStorage().size());
				if(getStorage().isdayu(ADD_LEVEL)){
					addOne(getInstance());
				}else if(getStorage().isxiaoyu(DEL_LEVEL) && canRemoveOne()){
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

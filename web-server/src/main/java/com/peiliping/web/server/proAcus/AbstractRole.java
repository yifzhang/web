package com.peiliping.web.server.proAcus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractRole<V> {
	
	@Getter
	protected ThreadPoolExecutor pool;
	@Getter
	protected IStorage<V> storage;
	@Getter
	protected int maxThreadsNum = 100 ;
	@Getter
	@Setter
	protected int minThreadsNum = 1 ;
	@Getter
	@Setter
	private String name;
	@Getter
	protected List<AbstractActionThread<V>>  threads ; 

	public AbstractRole(int concurrencyThreadsNum,IStorage<V> storage) {
		this.pool = new ThreadPoolExecutor(concurrencyThreadsNum,maxThreadsNum, 0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>()); 
		this.storage = storage;
		this.threads = new ArrayList<AbstractActionThread<V>>();
	}
	
	public synchronized void run(AbstractActionThread<V> ...tmp_threads) {
		if( tmp_threads != null && tmp_threads.length>0 && tmp_threads.length + threads.size() <= maxThreadsNum ){
			threads.addAll(Arrays.asList(tmp_threads));
			for (AbstractActionThread<V> p : tmp_threads) {
				pool.execute(p);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void run(int n){
		for(int i=0;i<n;i++)
			run(getInstance());
	}
	
	public synchronized void removeOne(){
		if(threads.size() == 0 || threads.size() <= minThreadsNum){
			return ;
		}
		AbstractActionThread<V> t = threads.remove(0);
		t.dropstatus = true ;
		pool.remove(t);
	}
	
	public synchronized void addOne(AbstractActionThread<V> thread){
		if( threads.size() + 1 <= maxThreadsNum ){;
			threads.add(thread);
			pool.execute(thread);
		}
	}
	
	public abstract AbstractActionThread<V> getInstance();
	
	public abstract class AbstractActionThread<R> implements Runnable {

		@Getter
		@Setter
		private IStorage<R> storage ;
		@Getter
		@Setter
		private long sleeptime ;
		
		public volatile boolean dropstatus = false ;
		
		public AbstractActionThread(IStorage<R> s,long sleeptime) {
			this.storage = s ;
			this.sleeptime = sleeptime ;
		}
		
		@Override
		public void run() {
			try {
				doAction();
			} catch (Throwable e) {
			}
		}
		
		public abstract void doAction();
		
	}
}

package com.peiliping.web.proAcus;

public abstract class Producers<V> extends AbstractRole<V> {

	public Producers(int concurrencyThreadsNum, IStorage<V> storage) {
		super(concurrencyThreadsNum ,storage);
	}
	
	public abstract ProducerActionThread<V> getInstance();

	
	public abstract class ProducerActionThread<K> extends AbstractActionThread<K> {

		public ProducerActionThread(IStorage<K> s,long sleeptime) {
			super(s,sleeptime);
		}

		@Override
		public void doAction() {
			while (true) {
				if (!getStorage().isFull()) {
					doProduce();
				}else{
					try {
						Thread.sleep(getSleeptime());
					} catch (InterruptedException e) {
					}
				}
			}
		}
		
		public abstract void doProduce();
	} 
}

package com.peiliping.web.server.dbtools.sequence;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;
import lombok.Setter;

public class DefaultSequence implements Sequence{
	
	protected final Lock lock = new ReentrantLock();

	@Setter
	@Getter
	private SequenceDao sequenceDao;
	@Setter
	@Getter
	private String name;

	protected volatile SequenceRange currentRange;

	public long nextValue(int index,int total) throws SequenceException {
		if (currentRange == null) {
			lock.lock();
			try {
				if (currentRange == null) {
					currentRange = sequenceDao.nextRange(name,index,total);
				}
			} finally {
				lock.unlock();
			}
		}
		long value = currentRange.getAndIncrement();
		if (value == -1) {
			lock.lock();
			try {
				for (;;) {
					if (currentRange.isOver()) {
						currentRange = sequenceDao.nextRange(name,index,total);
					}

					value = currentRange.getAndIncrement();
					if (value == -1) {
						continue;
					}

					break;
				}
			} finally {
				lock.unlock();
			}
		}
		if (value < 0) {
			throw new SequenceException("Sequence value overflow, value = " + value);
		}
		return value;
	}
}
package com.peiliping.web.server.dbtools.sequence;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;

public class SequenceRange {

	@Getter
	private final long min;
	@Getter
	private final long max;
	@Getter
	private volatile boolean over = false;

	private final AtomicLong value;

	public SequenceRange(long min, long max) {
		this.min = min;
		this.max = max;
		this.value = new AtomicLong(min);
	}

	public long getAndIncrement() {
		long currentValue = value.getAndIncrement();
		if (currentValue > max) {
			over = true;
			return -1;
		}
		return currentValue;
	}
}
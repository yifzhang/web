package com.peiliping.frame.sequence;

public interface Sequence {
	
	public long nextValue() throws SequenceException;

	public void setName(String name);
	
	public void setSequenceDao(SequenceDao sequenceDao);
	
	public SequenceDao getSequenceDao();
}

package com.peiliping.web.server.dbtools.sequence;

public interface Sequence {
	
	public long nextValue(int index ,int total) throws SequenceException;

	public void setName(String name);
	
	public void setSequenceDao(SequenceDao sequenceDao);
	
	public SequenceDao getSequenceDao();
}

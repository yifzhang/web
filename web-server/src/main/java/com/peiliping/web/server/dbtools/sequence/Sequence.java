package com.peiliping.web.server.dbtools.sequence;

public interface Sequence {
	
	 long nextValue(int index ,int total) throws SequenceException;

	 void setName(String name);
	
	 void setSequenceDao(SequenceDao sequenceDao);
	
	 SequenceDao getSequenceDao();
}

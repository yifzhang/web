package com.peiliping.web.server.dbtools.sequence;


public class GroupSequenceDao extends AbstractSequenceDao {

	@Override
	protected SequenceRange buildReturnRange(long newV,long oldV) {		
		return new SequenceRange(newV + 1, newV + getStep());
	}

	@Override
	protected long buildNewValue(long old, int index, int total) {		
		return old + getStep()*total;
	}

	@Override
	protected boolean check(long oldV, long newV, int index, int total) {
			return (newV %( getStep() * total)) == (index * getStep() + index + 1);
	}

}
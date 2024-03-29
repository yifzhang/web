package com.peiliping.web.server.dbtools.sequence;


public class DefaultSequenceDao extends AbstractSequenceDao {

	@Override
	protected long buildNewValue(long old, int index, int total) {
		return old + getStep();
	}

	@Override
	protected SequenceRange buildReturnRange(long newV, long oldV) {
		return new SequenceRange(oldV + 1, newV);
	}

	@Override
	protected boolean check(long oldV, long newV, int index, int total) {
		return true;
	}

}
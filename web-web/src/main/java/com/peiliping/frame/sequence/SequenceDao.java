package com.peiliping.frame.sequence;

import javax.sql.DataSource;

public interface SequenceDao {
	
	SequenceRange nextRange(String name) throws SequenceException;

	public void setDataSource(DataSource dataSource);
}

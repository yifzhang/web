package com.peiliping.web.server.dbtools.sequence;

import javax.sql.DataSource;

public interface SequenceDao {
	
	SequenceRange nextRange(String name) throws SequenceException;

	public void setDataSource(DataSource dataSource);
}

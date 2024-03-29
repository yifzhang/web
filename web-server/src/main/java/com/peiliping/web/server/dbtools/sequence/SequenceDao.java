package com.peiliping.web.server.dbtools.sequence;

import javax.sql.DataSource;

public interface SequenceDao {
	
	SequenceRange nextRange(String name,int index,int total) throws SequenceException;

	void setStep(int step);
	
	void setDataSource(DataSource dataSource);
}

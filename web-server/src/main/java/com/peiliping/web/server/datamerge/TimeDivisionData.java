package com.peiliping.web.server.datamerge;

public abstract class TimeDivisionData extends AbstractData {
	
	public TimeDivisionData(String name,long timestamp,long division,Object value) {
		super(name + timestamp/division, ((timestamp/division + 1)*division),value);
	}
}

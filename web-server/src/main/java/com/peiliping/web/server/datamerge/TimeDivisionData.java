package com.peiliping.web.server.datamerge;

public abstract class TimeDivisionData extends AbstractData {
	
	public TimeDivisionData(long id, String name, String type, double count,
			double cost, double max, double min,long timestamp,long division) {
		super(name, ((timestamp/division + 1)*division));
		super.name = super.name +  timestamp/division;
	}

}

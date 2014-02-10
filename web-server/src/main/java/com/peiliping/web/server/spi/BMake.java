package com.peiliping.web.server.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BMake implements Make {

	private static Logger logger = LoggerFactory.getLogger("web-log");
	
	@Override
	public void make() {
		logger.warn("B");
	}

}

package com.peiliping.web.controls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestControl {

	private static Logger logger = LoggerFactory.getLogger("web-log");
	
	@RequestMapping("/test.htm")
	 public String test(){
		logger.error("FFFF");
		 return "test";
	 }
}

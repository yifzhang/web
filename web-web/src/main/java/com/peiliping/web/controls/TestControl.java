package com.peiliping.web.controls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestControl {

	private static Logger l = LoggerFactory.getLogger("web-log");
	
	@RequestMapping("/teee.htm")
	 public String test(){
		
		l.error("FFFF");
		
		 return "teee";
	 }
}

package com.peiliping.web.controls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestControl {

	private static Logger logger = LoggerFactory.getLogger("web-log");
	
	@RequestMapping("/test.htm")
	 public String test(){
		logger.error("FFFF");
		 return "test";
	 }
	
	@RequestMapping("/testfm.htm")
	 public ModelAndView testfm(){
		logger.error("testfm");
		
		ModelAndView mv = new ModelAndView("testfm");
        mv.addObject("name", "My First Spring Mvc");
		
		 return mv;
	 }
}

package com.peiliping.web.controls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.peiliping.web.dao.KVDAO;
import com.peiliping.web.dataobject.KV;
import com.peiliping.web.tools.SpringApplicationContextHolder;

@Controller
public class TestControl {
	
	@Autowired
	private KVDAO kvDAO;
	

	private static Logger logger = LoggerFactory.getLogger("web-log");

	@RequestMapping("/test.htm")
	public String test() {
		logger.error("testjsp");
		return "test";
	}

	@RequestMapping("/testfm.htm")
	public ModelAndView testfm() {
		logger.error("testfm");
		ModelAndView mv = new ModelAndView("testfm");
		mv.addObject("name", "My First Spring Mvc");
		String[] l = SpringApplicationContextHolder.getBeanDefinitionNames();
		for(String i : l){
			logger.error(i);
		}
		return mv;
	}

	@RequestMapping("/testvm.htm")
	public ModelAndView testvm() {
		ModelAndView mv = new ModelAndView("testvm");
		try{
			logger.error("testvm");
			KV kv = kvDAO.getKV("1");
			mv.addObject("name", kv.getV());
		}catch (Exception e) {
			logger.error("sql",e);
		}
		return mv;
	}
	
	
}

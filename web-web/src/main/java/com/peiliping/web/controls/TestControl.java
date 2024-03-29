package com.peiliping.web.controls;

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.peiliping.web.server.dataobject.KV;
import com.peiliping.web.server.dbtools.sequence.SequenceException;
import com.peiliping.web.server.dbtools.sequence.SequenceService;
import com.peiliping.web.server.repository.KVRepo;
import com.peiliping.web.server.spi.M;
import com.peiliping.web.server.tools.MapX;
import com.peiliping.web.server.tools.SpringContextHolder;

@Controller
public class TestControl {

	@Autowired
	private KVRepo kvDAO;
	@Autowired
	private SequenceService seqservice;
	
	private static Logger logger = LoggerFactory.getLogger("web-log");

	@RequestMapping("/asynctest.htm")
	public Callable<String> testasync(){
	    return new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for(int i=0 ; i<100 ;i++)
                    kvDAO.getKV("1");
                logger.error("testjsp");
                M.m();
                return "test";
            }
	        
	    };
	}
	
	@RequestMapping("/test.htm")
	public String test() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i=0 ; i<100 ;i++)
            kvDAO.getKV("1");
		logger.error("testjsp");
		M.m();
		return "test";
	}

	@RequestMapping("/testfm.htm")
	public ModelAndView testfm() {
		logger.error("testfm");
		ModelAndView mv = new ModelAndView("testfm");
		mv.addObject("name", "My First Spring Mvc");
		String[] l = SpringContextHolder.getBeanDefinitionNames();
		for (String i : l) {
			logger.error(i);
		}
		return mv;
	}

	@RequestMapping("/testvm.htm")
	public ModelAndView testvm() {
		ModelAndView mv = new ModelAndView("testvm");
		try {
			logger.error("testvm");
			KV kv = kvDAO.getKV("1");
			mv.addObject("name", kv.getV());
		} catch (Throwable e) {
			logger.error("sql", e);
		}
		return mv;
	}

	@RequestMapping("/testinsert.htm")
	public ModelAndView testinsert() {
		ModelAndView mv = new ModelAndView("testvm");
		try {
			logger.error("testvm");
			int i = kvDAO.insert("wer", "vdc");
			mv.addObject("name", i);
		} catch (Throwable e) {
			logger.error("sql", e);
		}
		return mv;
	}
	
	@RequestMapping("/testlayout.htm")
	public ModelAndView testlayoutvm() {
		ModelAndView mv = new ModelAndView("testlayout");
		try {
			logger.error("testvm");
			KV kv = kvDAO.getKV("1");
			mv.addObject("name", kv.getV());
		} catch (Throwable e) {
			logger.error("sql", e);
		}
		return mv;
	}
	
	@RequestMapping("/json.do")
	@ResponseBody
	public Map<String, Object> testvm2() {
		return MapX.newMapXSO("a", "b").add("c", "d").add("e", "f");
	}
	
	@RequestMapping("/seq.do")
	@ResponseBody
	public Map<String, Object> seqvm2() throws SequenceException {
		long t = seqservice.nextValue();
		return MapX.newMapXSO("seq", (Object)t);
	}

}

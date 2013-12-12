package com.peiliping.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peiliping.web.monitor.IMonitorUtil;
import com.peiliping.web.monitor.MonitorResult;
import com.peiliping.web.monitor.MonitorUtil;

public class MonitorFilter implements Filter {

	private static final String PARA_MAXQPS = "maxqps";

	private static final String PARA_PROTECTED_MODE = "protected";
	
	private static final String PARA_STATISTICS_URI_DETAIL = "statisticsuridetail";

	private static final String LOG_MAIN_ITEM = "PV";

	private static Logger log;

	private IMonitorUtil mt;

	public static boolean PROTECTED_MODE = false;
	
	public static boolean STATISTICS_URI_DETAIL = false;

	public static int maxqps = 0;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log = LoggerFactory.getLogger(MonitorFilter.class);
		mt = new MonitorUtil(log,50 ,LOG_MAIN_ITEM);
		String mode = filterConfig.getInitParameter(PARA_PROTECTED_MODE);
		PROTECTED_MODE = Boolean.valueOf(mode);
		String urimode = filterConfig.getInitParameter(PARA_STATISTICS_URI_DETAIL);
		STATISTICS_URI_DETAIL = Boolean.valueOf(urimode);
		String limitqps = filterConfig.getInitParameter(PARA_MAXQPS);
		maxqps = (limitqps == null ? 0 : Integer.valueOf(limitqps));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {
		String uri = ((HttpServletRequest) request).getRequestURI();
		if (PROTECTED_MODE && mt.getActive(LOG_MAIN_ITEM) >= maxqps)			return;
		long starttime = System.currentTimeMillis();
		mt.in(LOG_MAIN_ITEM);
		if(STATISTICS_URI_DETAIL) mt.in(uri);
		boolean status = false;
		try {
			chain.doFilter(request, response);
			status = true;
		} finally {
			long costtime = System.currentTimeMillis() - starttime;
			mt.out(LOG_MAIN_ITEM, costtime, status ? MonitorResult.TYPE_SUCCESS	: MonitorResult.TYPE_FAILURE);
			if(STATISTICS_URI_DETAIL) mt.out(uri, costtime, status ? MonitorResult.TYPE_SUCCESS : MonitorResult.TYPE_FAILURE);
		}
	}

	@Override
	public void destroy() {
	}
}
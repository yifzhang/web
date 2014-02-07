package com.peiliping.web.server.dbtools.datasource;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicDataSourceFilter implements Filter {

	protected static Logger log = LoggerFactory.getLogger(DynamicDataSource.class);
	public static final String URI = "dynamicdatasource";
	private static final String PARAM_CMD = "cmd";
	private static final String PARAM_COMMOND_UPDATE = "update" ;
	public static final String PARAM_DSNAME = "dynamicdatasourcename";
	public static final String PARAM_TOKEN = "token" ;
		
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		String cmd = req.getParameter(PARAM_CMD);
		if(cmd.equals(PARAM_COMMOND_UPDATE)){
			DynamicDataSource d = DynamicDataSource.reg.get(req.getParameter(PARAM_DSNAME));
			if(d!=null){
				boolean status  = d.updateDataousrce((String)req.getParameter(PARAM_TOKEN));
				buildResponse(response, status ? "OK" : "ERROR");
			}else{
				buildResponse(response, "ERROR");
			}			
		}
	}
	
	public static void buildResponse(ServletResponse response,String result) throws IOException{
		 response.setContentType("text/html;charset=UTF-8");
	     PrintWriter writer = response.getWriter();
	     writer.println(result);
	}

	@Override
	public void destroy() {	}
}

/*
	<filter>
		<filter-name>DynamicDataSourceFilter</filter-name>
		<filter-class>com.peiliping.DataSource.DynamicDataSourceFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>DynamicDataSourceFilter</filter-name>
		<url-pattern>/dynamicdatasource</url-pattern>
	</filter-mapping> 
*/
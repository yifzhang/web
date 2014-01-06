package com.peiliping.frame.DataSource;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class DynamicDataSourceFilter implements Filter {

	public static final String URI = "dynamicdatasource";
	
	private static final String PARAM_CMD = "cmd";
	private static final String PARAM_DSNAME = "dsname";
	
	private static final String PARAM_COMMOND_UPDATE = "update" ;
	
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {
		String cmd = String.valueOf(request.getAttribute(PARAM_CMD));
		if(cmd.equals(PARAM_COMMOND_UPDATE)){
			DynamicDataSource d = DynamicDataSource.reg.get((String)request.getAttribute(PARAM_DSNAME));
			if(d!=null)
				d.updateDataousrce();
		}
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
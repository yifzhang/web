package com.peiliping.frame.DataSource;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class DynamicDataSourceFilter implements Filter {

	private static final String UPDATE_COMMOND = "dynamicdatasourceupdate";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {
		String uri = ((HttpServletRequest) request).getRequestURI();
		if(uri.equals(UPDATE_COMMOND)){
			DynamicDataSource d = DynamicDataSource.reg.get(request.getAttribute("dsname"));
			d.updateDataousrce();
		}
	}

	@Override
	public void destroy() {	}
}
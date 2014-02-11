package com.peiliping.web.server.subscriber;

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

import com.peiliping.web.server.subscriber.constants.MessageAction;

public class SubscriberFilter implements Filter {

	protected static Logger log = LoggerFactory.getLogger(SubscriberFilter.class);
	public static final String URI = "subscriber";
	private static final String PARAM_ID = "topicId";
	private static final String PARAM_ACTION = "action";
		
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		String id = req.getParameter(PARAM_ID);
		Subscriber s = Subscriber.getSubscriber(id);
		buildResponse(response, s==null ? "ERROR" : (s.getDataAndNotify(MessageAction.getMessageAction(Integer.valueOf(req.getParameter(PARAM_ACTION))))? "OK" : "ERROR"));
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
		<filter-name>SubscriberFilter</filter-name>
		<filter-class>com.peiliping.web.server.subscriber.SubscriberFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>SubscriberFilter</filter-name>
		<url-pattern>/subscriber</url-pattern>
	</filter-mapping> 
*/
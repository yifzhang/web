package com.peiliping.web.tools;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringApplicationContextHolder implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringApplicationContextHolder.context = applicationContext;
	}

	public static Object getSpringBean(String beanName) {
		return context.getBean(beanName);
	}

	public static String[] getBeanDefinitionNames() {
		return context.getBeanDefinitionNames();
	}

}

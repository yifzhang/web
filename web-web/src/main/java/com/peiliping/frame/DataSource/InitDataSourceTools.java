package com.peiliping.frame.DataSource;

import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class InitDataSourceTools {
	
	@SuppressWarnings("rawtypes")
	public static DataSource getDruidDataSource( Map properties) {
		try {
			return DruidDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {			
			//尝试两次 如果失败就算了。
			try {
				return DruidDataSourceFactory.createDataSource(properties);
			} catch (Exception e1) {
				//TODO 打印日志
			}
		}
		return null ;
	}

}

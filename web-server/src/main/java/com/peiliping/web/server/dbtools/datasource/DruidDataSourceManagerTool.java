package com.peiliping.web.server.dbtools.datasource;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.gson.reflect.TypeToken;

public class DruidDataSourceManagerTool extends IDataSourceManagerTool {
	
	@Override
	public DataSource createAinitDataSource( Map<String,String> properties){
		DataSource ds = null ;
		try {
			ds =  DruidDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {	
			log.error("init druid datasource errror",e);
			try {
				ds =  DruidDataSourceFactory.createDataSource(properties);
			} catch (Exception ex) {
				log.error("init druid datasource errror",ex);
				throw new IllegalArgumentException("Cannot Create Datasource" + GSON.toJson(properties,new TypeToken<Map<String,String>>(){}.getType()),ex);
			}
		}
		try {
			((DruidDataSource) ds).init();
		} catch (SQLException e) {
			log.error("init druid datasource errror" + GSON.toJson(properties, new TypeToken<Map<String,String>>() {}.getType()) ,e);
			throw new IllegalArgumentException("Init Datasource Failure"+ GSON.toJson(properties, new TypeToken<Map<String,String>>() {}.getType()), e);
		}
		return ds;
	}

	@Override
	public boolean destroyDataSource(DataSource datasource) {
		if (datasource != null && datasource instanceof DruidDataSource) {
			try {
				((DruidDataSource) datasource).close();
				return true;
			} catch (Throwable ex) {
				log.error("close datasource error : " ,  ex);
			}
		}
		return false;
	}
}

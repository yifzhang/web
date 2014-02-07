package com.peiliping.web.server.dbtools.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

public abstract class IDataSourceManagerTool {

	protected static HashMap<String,IDataSourceManagerTool> HandlerMap = new HashMap<String,IDataSourceManagerTool>();

	static{
		HandlerMap.put(DruidDataSource.class.getCanonicalName(), new DruidDataSourceManagerTool());
	}
	
	protected static Logger log = LoggerFactory.getLogger(DynamicDataSource.class);

	protected static Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();

	public abstract DataSource createAinitDataSource(Map<String,String> properties);
	
	public abstract boolean destroyDataSource(DataSource datasource) ;
	
	public static IDataSourceManagerTool getHandler(String clazzName){
		return HandlerMap.get(clazzName);
	}
	
	
	
}

package com.peiliping.frame.DataSource;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;

public class InitDataSourceTools {
	
	private static Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();
	
	@SuppressWarnings("rawtypes")
	public static DataSource getDruidDataSource( Map properties){
		DataSource ds = null ;
		try {
			ds =  DruidDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {	
			//TODO 打印日志
			//尝试两次。
			try {
				ds =  DruidDataSourceFactory.createDataSource(properties);
			} catch (Exception ex) {
				//TODO 打印日志
				throw new IllegalArgumentException("Cannot Create Datasource" + GSON.toJson(properties,new TypeToken<Map>(){}.getType()),ex);
			}
		}

		try {
			((DruidDataSource) ds).init();
		} catch (SQLException e) {
			throw new IllegalArgumentException("Init Datasource Failure"+ GSON.toJson(properties, new TypeToken<Map>() {}.getType()), e);
		}
		return ds;
	}

}

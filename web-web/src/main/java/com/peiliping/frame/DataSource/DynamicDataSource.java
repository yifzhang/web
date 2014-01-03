package com.peiliping.frame.DataSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.alibaba.druid.pool.DruidDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
	
	protected Map<Object, Object> tmp_targetDataSources = new HashMap<Object, Object>();

	@Getter
	@Setter
	protected String configserverUrl;

	@SuppressWarnings("rawtypes")
	@Override
	public void afterPropertiesSet() {
		Map<String,Map> map = getProperties();
		for(Entry<String,Map> e : map.entrySet()){
			DataSource ds = InitDataSourceTools.getDruidDataSource(e.getValue());
			tmp_targetDataSources.put(e.getKey(), ds);
		}
		super.setTargetDataSources(tmp_targetDataSources);
		super.afterPropertiesSet();
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceContextHolder.getDataSourceName();
	}
	
	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		tmp_targetDataSources.putAll(targetDataSources);
	}
	
	@SuppressWarnings("rawtypes")
	protected Map<String,Map> getProperties(){
		Map<String,Map> mp = new HashMap<String,Map>();
		//TODO  request configserverUrl & convert to map
		return mp ;
	}
	
	public void close(){
		for(Entry<Object,Object> e :  tmp_targetDataSources.entrySet()){
			if(e.getValue() instanceof DruidDataSource){
				try{
					((DruidDataSource)e.getValue()).close();					
				}catch(Throwable ex){
					//TODO 打印日志
				}
			}
		}
	}
}

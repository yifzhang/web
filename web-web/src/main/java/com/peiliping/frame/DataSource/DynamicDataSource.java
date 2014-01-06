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
	
	public static Map<String ,DynamicDataSource> reg = new HashMap<String, DynamicDataSource>();  
	
	protected Map<Object, Object> tmp_targetDataSources = new HashMap<Object, Object>();
	@Getter
	@Setter
	protected String configserverUrl;
	@Getter
	protected boolean updateListener = false ;  //接收动态更新数据源的通知
	@Getter
	@Setter
	protected int updateport = 8080  ;  //响应的端口
	@Getter
	@Setter
	protected String updateuri = null ;  //接收动态更新数据源的地址
	@Getter
	@Setter
	protected String dsName = null ;  //一组数据源的名字
	

	@SuppressWarnings("rawtypes")
	@Override
	public void afterPropertiesSet() {
		Map<String,Map> map = getProperties();
		if (map.size() > 0) {
			for (Entry<String, Map> e : map.entrySet()) {
				DataSource ds = InitDataSourceTools.getDruidDataSource(e.getValue());
				tmp_targetDataSources.put(e.getKey(), ds);
			}
		}
		super.setTargetDataSources(tmp_targetDataSources);
		super.afterPropertiesSet();
		reg.put(dsName,this);
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
	
	@SuppressWarnings("rawtypes")
	public boolean updateDataousrce(){
		Map<String,Map>  map = getProperties();
		if (map.size() > 0) {
			for (Entry<String, Map> e : map.entrySet()) {
				Object o = tmp_targetDataSources.get(e.getKey());
				tmp_targetDataSources.put(e.getKey(),InitDataSourceTools.getDruidDataSource(e.getValue()));
				if (o != null && o instanceof DruidDataSource) {
					try {
						((DruidDataSource) e.getValue()).close();
					} catch (Throwable ex) {
						// TODO 打印日志
					}
				}
			}
		}
		return true ;
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

	public void setUpdateListener(boolean updateListener) {
		this.updateListener = updateListener;
		//TODO  发送请求 给configserver 提交自己 ip:port/uri?dsName=dsName
	}
}

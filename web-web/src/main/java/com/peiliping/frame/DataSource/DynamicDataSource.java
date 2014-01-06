package com.peiliping.frame.DataSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

public class DynamicDataSource extends AbstractRoutingDataSource {
	
	private static Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();
	
	public static Map<String ,DynamicDataSource> reg = new HashMap<String, DynamicDataSource>();  
	
	protected Map<Object, Object> tmp_targetDataSources = new HashMap<Object, Object>();
	@Getter
	@Setter
	protected String configserver_host;
	@Getter
	@Setter
	protected String configserver_reg;
	@Getter
	@Setter
	protected String configserver_update;
	
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
		String result = httpconnnect(configserver_host + configserver_update  + "?dsname=" + dsName );
		mp = GSON.fromJson(result, new TypeToken<HashMap<String,Map>>(){}.getType() );
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
		httpconnnect(configserver_host + configserver_reg + "?dsname=" + dsName +"&port="+ updateport + "&uri" + updateuri + "&ip" + "");
	}
	
	public static String httpconnnect(String url) {
		//TODO 优化实现
		HttpURLConnection connection = null  ;
		try {
			URL u = new URL(url);
			connection = (HttpURLConnection) u.openConnection();
	        connection.setRequestMethod("GET");
	        connection.connect();
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        StringBuffer sb = new StringBuffer();
	        String content = "";
	        while ((content = br.readLine()) != null)
	        {
	            sb.append(content + "\n");
	        }
	        return sb.toString();
		} catch (Exception e) {
		}finally{
			if(connection !=null){
				connection.disconnect();
			}
		}
		return "";
	}
}

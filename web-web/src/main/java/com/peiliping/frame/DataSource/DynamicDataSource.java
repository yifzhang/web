package com.peiliping.frame.DataSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

public class DynamicDataSource extends AbstractRoutingDataSource {
	
	protected static Logger log = LoggerFactory.getLogger(DynamicDataSource.class);
	
	private static Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();
	
	public static Map<String ,DynamicDataSource> reg = new HashMap<String, DynamicDataSource>();  
	
	protected Map<Object, Object> tmp_targetDataSources = new HashMap<Object, Object>();
	@Getter
	@Setter
	protected String configserver_host = "http://192.168.1.222"; 
	@Getter
	@Setter
	protected String configserver_reg = "/regist?";     // 
	@Getter
	@Setter
	protected String configserver_datasource = "/datasource?";
	
	@Getter
	protected boolean updateListener = false ;  //接收动态更新数据源的通知
	@Getter
	@Setter
	protected int updateport = 8080  ;  //响应的端口
	@Getter
	@Setter
	protected String updateuri = DynamicDataSourceFilter.URI ;  //接收动态更新数据源的地址
	@Getter
	@Setter
	protected String dynamicDataSourceName = null ;  //动态数据源的名字
	@Getter
	@Setter
	protected boolean needRemote = false ;  //是否需要远程获取数据源
	
	public static final String TOKEN_GET_ALL = "getall";
	

	@SuppressWarnings("rawtypes")
	@Override
	public void afterPropertiesSet() {
		if(StringUtils.isBlank(dynamicDataSourceName)){
			Validate.notNull(null,"dynamicDataSourceName is null");
		}
		Map<String,Map> map = getProperties(TOKEN_GET_ALL);
		if (map.size() > 0) {
			for (Entry<String, Map> e : map.entrySet()) {
				DataSource ds = InitDataSourceTools.getDruidDataSource(e.getValue());
				tmp_targetDataSources.put(e.getKey(), ds);
			}
		}
		super.setTargetDataSources(tmp_targetDataSources);
		super.afterPropertiesSet();
		reg.put(dynamicDataSourceName,this);
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceContextHolder.getDataSourceName();
	}
	
	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		tmp_targetDataSources.putAll(targetDataSources);
	}
	
	@SuppressWarnings({ "rawtypes", "serial" })
	protected Map<String,Map> getProperties(String token){
		Map<String,Map> mp = new HashMap<String,Map>();
		if(!needRemote){ return mp;	}
		String result = httpconnnect(configserver_host + configserver_datasource  + DynamicDataSourceFilter.PARAM_DSNAME + "=" + dynamicDataSourceName  + "&token=" + token );
		mp = GSON.fromJson(result, new TypeToken<HashMap<String,Map>>(){}.getType() );
		log.warn("Dynamic Data Source Property : " + result );
		return mp ;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean updateDataousrce(String token) {
		Map<String, Map> map = getProperties(token);
		if (map == null || map.size() == 0) {
			return false;
		}
		for (Entry<String, Map> e : map.entrySet()) {
			Object o = tmp_targetDataSources.get(e.getKey());
			tmp_targetDataSources.put(e.getKey(),InitDataSourceTools.getDruidDataSource(e.getValue()));
			if (o != null && o instanceof DruidDataSource) {
				try {
					((DruidDataSource) e.getValue()).close();
				} catch (Throwable ex) {
					log.error("close datasource error : " + e.getKey(), ex);
				}
			}
		}
		return true;
	}
	
	public void close(){
		for(Entry<Object,Object> e :  tmp_targetDataSources.entrySet()){
			if(e.getValue() instanceof DruidDataSource){
				try{
					((DruidDataSource)e.getValue()).close();					
				}catch(Throwable ex){
					log.error("close datasource error : " + e.getKey()  ,ex);
				}
			}
		}
	}

	public void setUpdateListener(boolean updateListener) {
		this.updateListener = updateListener;
		if(updateListener){
			httpconnnect(configserver_host + configserver_reg + 
					DynamicDataSourceFilter.PARAM_DSNAME + "="  + dynamicDataSourceName +"&port="+ updateport + "&uri=" + updateuri + "&ip=" + getLocalIP());
		}
	}
	
	public static String getLocalIP(){
		String localIP = null;
		String netIP = null;
		Enumeration<NetworkInterface> nInterfaces = null;
		try {
			nInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {		}
		boolean finded = false;
		while(nInterfaces.hasMoreElements() && !finded){
			Enumeration<InetAddress> inetAddress = nInterfaces.nextElement().getInetAddresses();
			while(inetAddress.hasMoreElements()){
				InetAddress address = inetAddress.nextElement();
				if(!address.isSiteLocalAddress() && !address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1){
					netIP = address.getHostAddress();
					finded = true;
					break;
				}else if(address.isSiteLocalAddress() && !address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1){
					localIP = address.getHostAddress();
				}
			}
		}
		return (netIP != null && !"".equals(netIP)) ? netIP : localIP ; 
	}
	
	public static String httpconnnect(String url) {
		int trytimes = 3;
		while (trytimes > 0) {
			HttpURLConnection connection = null;
			try {
				URL u = new URL(url);
				connection = (HttpURLConnection) u.openConnection();
				connection.setRequestMethod("GET");
				connection.connect();
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String content = "";
				while ((content = br.readLine()) != null) {
					sb.append(content + "\n");
				}
				return sb.toString();
			} catch (Exception e) {
				trytimes--;
				log.error("httpconnect error : " + url, e);
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
		throw new IllegalArgumentException("Try my best,but failed![" + url + "]" );
	}
	
}

package com.peiliping.web.server.dbtools.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.peiliping.web.server.subscriber.Subscriber;
import com.peiliping.web.server.subscriber.SubscriberListener;
import com.peiliping.web.server.subscriber.constants.MessageAction;
import com.peiliping.web.server.subscriber.entity.Topic;

public class DynamicDataSource extends AbstractRoutingDataSource {
	
	protected static Logger log = LoggerFactory.getLogger(DynamicDataSource.class);
	
	private static Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();
	
	public static Map<String ,DynamicDataSource> reg = new HashMap<String, DynamicDataSource>();  
	@Getter
	@Setter
	protected Subscriber subscriber;
	@Getter
	protected Map<Object, Object> tmp_targetDataSources = new HashMap<Object, Object>();
	@Getter
	@Setter
	protected String dynamicDataSourceName = null ;  //动态数据源的名字
	@Getter
	@Setter
	protected boolean needRemote = false ;  //是否需要远程获取数据源
	@Getter
	@Setter
	protected String dataSourceClassName = DruidDataSource.class.getCanonicalName() ;
	@Getter
	@Setter
	private DynamicDataSourceUpdateListener listener;//TODO 改成list

	private DataSource tmp_defaultTargetDataSource ;	
	private static final String DEFAULT_KEY = "DEFAULT";
	
	@Override
	public void afterPropertiesSet() {
		if(StringUtils.isBlank(dynamicDataSourceName)){
			Validate.notNull(null,"dynamicDataSourceName is null");
		}
		Map<String,Map<String,String>> map = getProperties(MessageAction.Message_Action_Init);
		if (map.size() > 0) {
			for (Entry<String, Map<String,String>> e : map.entrySet()) {
				DataSource ds =  IDataSourceManagerTool.getHandler(dataSourceClassName).createAinitDataSource(e.getValue());
				if(DEFAULT_KEY.equals(e.getKey())){
					setDefaultTargetDataSource(ds);
				}else{
					tmp_targetDataSources.put(e.getKey(), ds);
				}
			}
		}
		super.setTargetDataSources(tmp_targetDataSources);
		super.afterPropertiesSet();
		reg.put(dynamicDataSourceName,this);
		if (subscriber != null && needRemote) {
			subscriber.addListeners(new SubscriberListener() {
				@Override
				public boolean init(Topic topic) {return true;}
				@Override
				public boolean reload(Topic topic) {updateDataousrce(MessageAction.Message_Action_Reload);return false;}
				@Override
				public boolean update(Topic topic) {updateDataousrce(MessageAction.Message_Action_Update);return false;}
				@Override
				public boolean delete(Topic topic) {return true;}
			});
		}
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceContextHolder.getDataSourceName();
	}
	
	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		tmp_targetDataSources.putAll(targetDataSources);
	}
	
	@SuppressWarnings("serial")
	protected Map<String,Map<String,String>> getProperties(MessageAction ma){
		Map<String,Map<String,String>> mp = new HashMap<String,Map<String,String>>();
		if(!needRemote){ return mp;	}
		String result = subscriber.getTopic().getCurrentMessage(ma).getMessageBody();
		mp = GSON.fromJson(result, new TypeToken<HashMap<String,Map<String,String>>>(){}.getType() );
		log.warn("Dynamic Data Source Property : " + result );
		return mp ;
	}
	
	private boolean updateDataousrce(MessageAction ma) {
		Map<String, Map<String,String>> map = getProperties(ma);
		if (map == null || map.size() == 0) {
			return false;
		}
		for (Entry<String, Map<String,String>> e : map.entrySet()) {
			Object o = DEFAULT_KEY.equals(e.getKey())?tmp_defaultTargetDataSource :tmp_targetDataSources.get(e.getKey());
			if(DEFAULT_KEY.equals(e.getKey())){
				setDefaultTargetDataSource(IDataSourceManagerTool.getHandler(dataSourceClassName).createAinitDataSource(e.getValue()));
			}else{
				tmp_targetDataSources.put(e.getKey(), IDataSourceManagerTool.getHandler(dataSourceClassName).createAinitDataSource(e.getValue()));
			}
			if(listener!=null){
				listener.call(e.getKey(), DEFAULT_KEY.equals(e.getKey()) ? tmp_defaultTargetDataSource : (DataSource)tmp_targetDataSources.get(e.getKey()),(DataSource)o);
			}
			IDataSourceManagerTool.getHandler(dataSourceClassName).destroyDataSource((DataSource)o);
		}
		super.afterPropertiesSet();
		return true;
	}
	
	public void close(){
		for(Entry<Object,Object> e :  tmp_targetDataSources.entrySet()){
			IDataSourceManagerTool.getHandler(dataSourceClassName).destroyDataSource((DataSource)e.getValue());
		}
		IDataSourceManagerTool.getHandler(dataSourceClassName).destroyDataSource(tmp_defaultTargetDataSource);
	}
	
	@Override
	public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
		tmp_defaultTargetDataSource = (DataSource)defaultTargetDataSource;
		super.setDefaultTargetDataSource(defaultTargetDataSource);
	}
	
	public interface DynamicDataSourceUpdateListener{
		void call(String k , DataSource dnew ,DataSource dold);
	}
}

package com.peiliping.web.server.dbtools.datasource;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class Interceptor4TableName implements Interceptor {

	private final static Pair<String,String> SKIP = new MutablePair<String,String>();
	
	private Map<String,Pair<String,String>> cacheIdvsTableName = new HashMap<String, Pair<String,String>>();
	
	private Map<String,TablenameHandler> cacheTableNamevsHandler = new HashMap<String, TablenameHandler>();
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		MappedStatement mappedStatement = null;
		if (statementHandler instanceof RoutingStatementHandler) {
			StatementHandler delegate = (StatementHandler) ReflectionUtils.getFieldValue(statementHandler, "delegate");
			mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(delegate, "mappedStatement");
		} else {
			mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(statementHandler, "mappedStatement");
		}
		String mapperId = mappedStatement.getId();
		Object params = statementHandler.getBoundSql().getParameterObject();
		Pair<String,String> tableNameAndType = buildTableName(mapperId,statementHandler); 
		if(tableNameAndType != null && StringUtils.isNotBlank(tableNameAndType.getLeft()) &&
				cacheTableNamevsHandler.get(tableNameAndType.getLeft())!=null){
			String newTableName = cacheTableNamevsHandler.get(tableNameAndType.getLeft()).getTargetTableName(
					tableNameAndType.getRight(), tableNameAndType.getRight(), params, mapperId);
			String sql = statementHandler.getBoundSql().getSql();
			if(StringUtils.isNotBlank(sql)&&StringUtils.isNotBlank(newTableName)){
				sql.replaceAll(tableNameAndType.getLeft(),newTableName);
				ReflectionUtils.setFieldValue(statementHandler.getBoundSql(), "sql", sql);
			}
		}
		return invocation.proceed();
	}
	
	private Pair<String,String> buildTableName(String id,StatementHandler statementHandler){
		Pair<String,String> tn = cacheIdvsTableName.get(id);
		if(tn == null){
			tn = SQLParser.findTableNameAndType(statementHandler.getBoundSql().getSql());			
			cacheIdvsTableName.put(id, tn==null ? SKIP : tn);
		}
		return tn == SKIP ? null : tn ;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		if(properties == null || properties.size() == 0) return ;
		for(Entry<Object, Object> e : properties.entrySet()){
			Object o = null;
			try {
				Class<?> c = Class.forName((String)e.getValue());
				o = c.newInstance();
				Validate.isTrue(o instanceof TablenameHandler);
			} catch (Exception ec) {
				Validate.isTrue(false,ec.toString());
			}
			cacheTableNamevsHandler.put((String)e.getKey(),(TablenameHandler)o);
		}
	}
}
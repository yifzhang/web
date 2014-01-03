package com.peiliping.web.server;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class Test4DruidInit {
	
	public static void main(String[] args) throws Exception {
		
		Map<String,String> props = new HashMap<String,String>();
		props.put("driverClassName", "com.mysql.jdbc.Driver");
		props.put("url", "jdbc:mysql://1.1.1.1:11111/test");
		props.put("username", "xxxx");
		props.put("password", "xxxx");
		
		DataSource ds = DruidDataSourceFactory.createDataSource(props);
		Statement statement = ds.getConnection().createStatement();
		ResultSet resultSet =  statement.executeQuery("select * from kv");
		while(resultSet.next()){
			System.out.println(resultSet.getString("k"));
		}
		
	}

}

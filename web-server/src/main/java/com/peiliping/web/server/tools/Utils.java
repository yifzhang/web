package com.peiliping.web.server.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
	
	public static String httpconnnect(String url) {
		int trytimes = 3;
		while (trytimes > 0) {
			HttpURLConnection connection = null;
			try {
				URL u = new URL(url);
				connection = (HttpURLConnection) u.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(100);
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
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
		throw new IllegalArgumentException("Try my best,but failed![" + url + "]" );
	}
}

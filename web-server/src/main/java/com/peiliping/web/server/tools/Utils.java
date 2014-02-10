package com.peiliping.web.server.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class Utils {

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

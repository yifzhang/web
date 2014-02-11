package com.peiliping.web.server.subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.InitializingBean;

import com.peiliping.web.server.subscriber.constants.MessageAction;
import com.peiliping.web.server.subscriber.entity.Topic;
import com.peiliping.web.server.tools.HttpUtil;
import com.peiliping.web.server.tools.HttpUtil.HttpResult;

public class Subscriber implements InitializingBean{
	
	private final static Map<String,Subscriber> REG = new HashMap<String, Subscriber>();
	
	public static Subscriber getSubscriber(String topicId){
		return REG.get(topicId);
	}
	
	private String localIp = HttpUtil.getLocalIP();
	@Setter
	@Getter
	private String serverHost = "127.0.0.1";	
	@Setter
	@Getter
	private String topicId = "test";
	@Setter
	@Getter
	private boolean needRegist = true ;
	@Getter
	private Topic topic ;
	
	private String buildRegistUrl(){
		return "http://" + serverHost + "/subscribe?ip=" + localIp + "&topicId=" + topicId; 
	}
	
	private String buildGetDataUrl(String action){
		return "http://" + serverHost + "/getdata?ip=" + localIp + "&topicId=" + topicId + "&action=" + action; 
	}
	
	private List<SubscriberListener> listeners = new ArrayList<SubscriberListener>(); 
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Validate.isTrue(StringUtils.isNotBlank(topicId),"topicId is blank");
		if(needRegist){
			HttpResult hr = HttpUtil.httpGet(buildRegistUrl());
			Validate.isTrue(hr.success, hr.content);
			topic = Topic.build(hr.content);
		}else{
			topic = new Topic();
			topic.setId(topicId);
		}
		Validate.isTrue(getDataAndNotify(MessageAction.Message_Action_Init),topicId + " Subscriber Init Failed");
		REG.put(topicId, this);
	}
	
	public synchronized boolean getDataAndNotify(MessageAction ma){
		String action = ma.getContent();
		HttpResult hr = HttpUtil.httpGet(buildGetDataUrl(action));
		Validate.isTrue(hr.success,hr.content);
		boolean r = topic.addMessage(hr.content);
		if(!r) return true ;
		for(SubscriberListener li : listeners){
			if(MessageAction.Message_Action_Init.getContent().equals(action)){
				li.init(topic);
			}else if(MessageAction.Message_Action_Reload.getContent().equals(action)){
				li.reload(topic);
			}else if(MessageAction.Message_Action_Update.getContent().equals(action)){
				li.update(topic);
			}else if(MessageAction.Message_Action_Delete.getContent().equals(action)){
				li.delete(topic);
			}
		}
		return true;
	}
	
	public void addListeners(SubscriberListener ...ls){
		if(ls==null){return ;}
		for(SubscriberListener l : ls){
			listeners.add(l);
		}
	}
}
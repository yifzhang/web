package com.peiliping.web.server.subscriber.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.Validate;

import lombok.Getter;
import lombok.Setter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;
import com.peiliping.web.server.subscriber.constants.MessageAction;
import com.peiliping.web.server.subscriber.constants.TopicType;

public class Topic {
	private static Gson GSON = new GsonBuilder().
			setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();
	private final static int LIST_SIZE = 5 ;
	@Setter
	@Getter
	private String id  ;
	@Setter
	@Getter
	private TopicType type ; 
	@Setter
	@Getter
	private String description;
	
	private ConcurrentHashMap<Integer, List<Message>>  messages = new ConcurrentHashMap<Integer, List<Message>>();
	
	{
		messages.put(MessageAction.Message_Action_Init.getKey(),   new ArrayList<Message>() );
		messages.put(MessageAction.Message_Action_Reload.getKey(), new ArrayList<Message>() );
		messages.put(MessageAction.Message_Action_Update.getKey(), new ArrayList<Message>() );
		messages.put(MessageAction.Message_Action_Delete.getKey(), new ArrayList<Message>() );
	}

	public static Topic build(String json){
		Map<String, String> m = GSON.fromJson(json, new TypeToken<HashMap<String,String>>(){}.getType() );
		Validate.isTrue(Boolean.valueOf(m.get("success")),m.get("reason"));
		Topic t = new Topic();
		t.setId(m.get("id"));
		t.setType(TopicType.getTopic(Integer.valueOf(m.get("type"))));
		t.setDescription(m.get("description"));
		return t;
	}
	
	public synchronized boolean addMessage(String json){
		Message ms = Message.build(json);
		List<Message> l = messages.get(ms.getAction().getKey());
		if(l.size()>0 && l.get(l.size()-1).getVersion() >= ms.getVersion()){
			return false;
		}
		l.add(ms);
		Collections.sort(l, new Comparator<Message>() {
			@Override
			public int compare(Message o1, Message o2) {
				return o1.getVersion()>o2.getVersion() ? -1 : 1 ;
			}
		});
		if(l.size() > LIST_SIZE){
			l.remove(0);
		}
		return true;
	}
	
	public synchronized Message getCurrentMessage(MessageAction ma){
		List<Message> list = messages.get(ma.getKey());
		return list.size() == 0 ? null : list.get(list.size()-1);
	}
}

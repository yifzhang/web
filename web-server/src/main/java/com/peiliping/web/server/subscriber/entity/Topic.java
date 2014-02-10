package com.peiliping.web.server.subscriber.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
		Topic t = new Topic();
		t.setId(m.get("id"));
		t.setType(TopicType.getTopic(Integer.valueOf(m.get("type"))));
		t.setDescription(m.get("description"));
		return t;
	}
	
	public synchronized void addMessage(String json){
		Message ms = Message.build(json);
		messages.get(ms.getAction().getKey()).add(ms);
		Collections.sort(messages.get(ms.getAction()), new Comparator<Message>() {
			@Override
			public int compare(Message o1, Message o2) {
				return o1.getVersion()>o2.getVersion() ? -1 : 1 ;
			}
		});
		if(messages.get(ms.getAction()).size() > LIST_SIZE){
			messages.get(ms.getAction()).remove(0);
		}
	}
	
	public synchronized Message getCurrentMessage(MessageAction ma){
		List<Message> list = messages.get(ma.getKey());
		return list.size() == 0 ? null : list.get(list.size()-1);
	}
}

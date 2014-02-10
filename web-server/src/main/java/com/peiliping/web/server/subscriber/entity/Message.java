package com.peiliping.web.server.subscriber.entity;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;
import com.peiliping.web.server.subscriber.constants.MessageAction;

public class Message {
	private static Gson GSON = new GsonBuilder().
			setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();
	@Setter
	@Getter
	private String topicId;
	@Setter
	@Getter
	private long version;
	@Setter
	@Getter
	private MessageAction action ;
	@Setter
	@Getter
	private String messageBody;	

	public static Message build(String json){
		Map<String, String> m = GSON.fromJson(json, new TypeToken<HashMap<String,String>>(){}.getType() );
		Message ms = new Message();
		ms.setTopicId(m.get("topicId"));
		ms.setVersion(Long.valueOf(m.get("version")));
		ms.setMessageBody(m.get("content"));
		ms.setAction(MessageAction.getMessageAction(Integer.valueOf(m.get("action"))));
		return ms;
	}
}

package com.peiliping.web.server.subscriber.constants;

import lombok.Getter;

public enum TopicType {
	
	Topic_Type_DataSource(1,"datasource"),
	Topic_Type_Switch(2,"switch")
	;

	@Getter
	private final int key;
	@Getter
	private final String content;
	
	private TopicType(int key ,String content) {
		this.key = key ;
		this.content = content ;
	}
	
	public static TopicType getTopic(int key){
		for(TopicType tt: TopicType.values()){
			if(tt.getKey() == key ){
				return tt ;
			}
		}
		return null ;
	}
}

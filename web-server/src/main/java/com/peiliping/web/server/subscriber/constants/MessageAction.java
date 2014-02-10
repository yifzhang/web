package com.peiliping.web.server.subscriber.constants;

import lombok.Getter;

public enum MessageAction {

	Message_Action_Init(1,"init"),
	Message_Action_Reload(2,"reload"),
	Message_Action_Update(3,"update"),
	Message_Action_Delete(4,"delete"),
	;
	
	@Getter
	private final int key;
	@Getter
	private final String content;
	
	private MessageAction(int key ,String content) {
		this.key = key ;
		this.content = content ;
	}
	
	public static MessageAction getMessageAction(int key){
		for(MessageAction ma: MessageAction.values()){
			if(ma.getKey() == key ){
				return ma ;
			}
		}
		return null ;
	}
}

package com.peiliping.web.server;

import com.peiliping.web.server.subscriber.Subscriber;
import com.peiliping.web.server.subscriber.constants.MessageAction;
import com.peiliping.web.server.subscriber.entity.Message;

public class SubscriberTest {

	public static void main(String[] args) throws Exception {		
		Subscriber ss = new Subscriber();
		ss.afterPropertiesSet();	
		ss.getDataAndNotify(MessageAction.Message_Action_Init);
		Message m = ss.getTopic().getCurrentMessage(MessageAction.Message_Action_Init);
		System.out.println(m.getMessageBody());
	}
}
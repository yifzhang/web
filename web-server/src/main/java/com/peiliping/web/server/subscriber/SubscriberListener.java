package com.peiliping.web.server.subscriber;

import com.peiliping.web.server.subscriber.entity.Topic;

public interface SubscriberListener {
	
	boolean init(Topic topic);
	
	boolean reload(Topic topic);
	
	boolean update(Topic topic);
	
	boolean delete(Topic topic);

}

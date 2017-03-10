package com.javachina.service;

import com.javachina.model.TopicCount;

public interface TopicCountService {
	
	TopicCount getCount(Integer tid);
	
	void update(String type, Integer tid, int count);

	void save(Integer tid, Integer create_time);
	
}

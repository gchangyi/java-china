package com.javachina.service;

import com.blade.jdbc.model.Paginator;

import java.util.Map;

public interface NoticeService {
	
	boolean save(String type, Integer uid, Integer to_uid, Integer event_id);
	
	boolean read(Integer to_uid);

	Paginator<Map<String, Object>> getNoticePage(Integer to_uid, Integer page, Integer count);

	Integer getNotices(Integer to_uid);
	
}

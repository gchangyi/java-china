package com.javachina.service;

import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.javachina.model.Comment;

import java.util.Map;

public interface CommentService {
	
	Comment getComment(Integer cid);
		
	Comment getTopicLastComment(Integer tid);
	
	Paginator<Map<String, Object>> getPageListMap(Take take);

	Integer save(Integer uid, Integer toUid, Integer tid, String content, String ua);
	
	boolean delete(Integer cid);

	Integer getComments(Integer uid);
		
}

package com.javachina.service;

import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.javachina.dto.HomeTopic;
import com.javachina.model.Topic;

import java.util.List;
import java.util.Map;

public interface TopicService {
	
	Topic getTopic(Integer tid);
	
	Map<String, Object> getTopicMap(Topic topic, boolean isDetail);
	
	Paginator<Map<String, Object>> getPageList(Take queryParam);

	Integer save(Topic topic);

	Integer update(Integer tid, Integer nid, String title, String content);
	
	boolean comment(Integer uid, Integer to_uid, Integer tid, String content, String ua);
	
	void delete(Integer tid);
	
	void refreshWeight();

	void updateWeight(Integer tid);

	void updateWeight(Integer tid, Integer loves, Integer favorites, Integer comment, Integer sinks, Integer create_time);

	Integer getTopics(Integer uid);

	Integer getLastCreateTime(Integer uid);

	Integer getLastUpdateTime(Integer uid);

	void essence(Integer tid, Integer count);

	/**
	 * 获取首页帖
	 *
	 * @param nid
	 * @param page
	 * @param limit
	 * @return
	 */
	Paginator<HomeTopic> getHomeTopics(Integer nid, int page, int limit);

	/**
	 * 获取最新帖
	 *
	 * @param nid
	 * @param page
	 * @param limit
	 * @return
	 */
	Paginator<HomeTopic> getRecentTopics(Integer nid, int page, int limit);

	/**
	 * 获取精华贴
	 *
	 * @param page
	 * @param limit
	 * @return
	 */
	Paginator<HomeTopic> getEssenceTopics(int page, int limit);

	/**
	 * 获取热门贴
	 *
	 * @param page
	 * @param limie
	 * @return
	 */
	List<HomeTopic> getHotTopics(int page, int limie);
}

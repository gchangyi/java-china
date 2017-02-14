package com.javachina.service.impl;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.ActiveRecord;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.javachina.exception.TipException;
import com.javachina.model.TopicCount;
import com.javachina.service.TopicCountService;

@Service
public class TopicCountServiceImpl implements TopicCountService {

	@Inject
	private ActiveRecord activeRecord;

	@Override
	public void update(String type, Integer tid, int count) throws Exception {
		if(StringKit.isBlank(type) || null == tid){
			throw new TipException("帖子id为空");
		}
		try {
			String sql = String.format("update t_topiccount set %s = (%s + "+count+") where tid = " + tid, type, type);
			activeRecord.execute(sql);
		} catch (Exception e){
			throw e;
		}
	}

	@Override
	public TopicCount getCount(Integer tid) {
		if(null == tid){
			return null;
		}
		return activeRecord.byId(TopicCount.class, tid);
	}

	@Override
	public void save(Integer tid, Integer create_time) throws Exception {
		try {
			if(null == tid || tid < 1){
				throw new TipException("帖子id为空");
			}
			TopicCount topicCount = new TopicCount();
			topicCount.setTid(tid);
			topicCount.setViews(0);
			topicCount.setFavorites(0);
			topicCount.setComments(0);
			topicCount.setSinks(0);
			topicCount.setCreate_time(DateKit.getCurrentUnixTime());
			activeRecord.insert(topicCount);
		} catch (Exception e) {
			throw e;
		}
	}

}

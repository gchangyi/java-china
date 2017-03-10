package com.javachina.service.impl;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.ActiveRecord;
import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.blade.kit.DateKit;
import com.javachina.ext.Funcs;
import com.javachina.kit.Utils;
import com.javachina.model.Comment;
import com.javachina.model.Topic;
import com.javachina.model.User;
import com.javachina.service.CommentService;
import com.javachina.service.TopicService;
import com.javachina.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

	@Inject
	private ActiveRecord activeRecord;

	@Inject
	private UserService userService;
	
	@Inject
	private TopicService topicService;
	
	@Override
	public Comment getComment(Integer cid) {
		return activeRecord.byId(Comment.class, cid);
	}
	
	@Override
	public Paginator<Map<String, Object>> getPageListMap(Take take) {
		if(null != take){
			Paginator<Comment> commentPage = activeRecord.page(take);
			return this.getCommentPageMap(commentPage);
		}
		return null;
	}
	
	private Paginator<Map<String, Object>> getCommentPageMap(Paginator<Comment> commentPage){
		
		long totalCount = commentPage.getTotal();
		int page = commentPage.getPageNum();
		int pageSize = commentPage.getLimit();
		Paginator<Map<String, Object>> result = new Paginator<Map<String,Object>>(totalCount, page, pageSize);
		
		List<Comment> comments = commentPage.getList();
		
		List<Map<String, Object>> nodeMaps = new ArrayList<Map<String,Object>>();
		if(null != comments && comments.size() > 0){
			for(Comment comment : comments){
				Map<String, Object> map = this.getCommentDetail(comment, null);
				if(null != map && !map.isEmpty()){
					nodeMaps.add(map);
				}
			}
		}
		
		result.setList(nodeMaps);
		
		return result;
	}
	
	private Map<String, Object> getCommentDetail(Comment comment, Integer cid) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(null == comment){
			comment = this.getComment(cid);
		}
		if(null != comment){
			
			Integer comment_uid = comment.getUid();
			User comment_user = userService.getUser(comment_uid);
			Topic topic = topicService.getTopic(comment.getTid());
			if(null == comment_user || null == topic){
				return map;
			}

			map.put("cid", comment.getCid());
			map.put("tid", comment.getTid());
			map.put("role_id", comment_user.getRole_id());
			map.put("reply_name", comment_user.getLogin_name());
			map.put("reply_time", comment.getCreate_time());
			map.put("device", comment.getDevice());
			map.put("reply_avatar", Funcs.avatar_url(comment_user.getAvatar()));
			map.put("title", topic.getTitle());
			
			String content = Utils.markdown2html(comment.getContent());
			map.put("content", content);
		}
		return map;
	}

	@Override
	public Integer save(Integer uid, Integer toUid, Integer tid, String content, String ua) {
		try {
			Comment comment = new Comment();
			comment.setUid(uid);
			comment.setTo_uid(toUid);
			comment.setTid(tid);
			comment.setContent(content);
			comment.setDevice(ua);
			comment.setCreate_time(DateKit.getCurrentUnixTime());

			Long cid = activeRecord.insert(comment);
			return cid.intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean delete(Integer cid) {
		if(null != cid){
			return activeRecord.delete(Comment.class, cid) > 0;
		}
		return false;
	}

	@Override
	public Comment getTopicLastComment(Integer tid) {
		Take take = new Take(Comment.class);
		take.eq("tid", tid).desc("cid");
		return activeRecord.one(take);
	}

	@Override
	public Integer getComments(Integer uid) {
		if(null != uid){
			Comment comment = new Comment();
			comment.setUid(uid);
			return activeRecord.count(comment);
		}
		return 0;
	}
		
}

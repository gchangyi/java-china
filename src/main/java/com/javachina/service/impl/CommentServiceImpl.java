package com.javachina.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.AR;
import com.blade.jdbc.Page;
import com.blade.jdbc.QueryParam;
import com.javachina.ImageTypes;
import com.javachina.ext.markdown.Processor;
import com.javachina.kit.ImageKit;
import com.javachina.model.Comment;
import com.javachina.model.User;
import com.javachina.service.CommentService;
import com.javachina.service.UserService;

import blade.kit.DateKit;

@Service
public class CommentServiceImpl implements CommentService {
	
	@Inject
	private UserService userService;
	
	@Override
	public Comment getComment(Long cid) {
		return AR.findById(Comment.class, cid);
	}
		
	@Override
	public List<Comment> getCommentList(QueryParam queryParam) {
		if(null != queryParam){
			return AR.find(queryParam).list(Comment.class);
		}
		return null;
	}
	
	@Override
	public Page<Map<String, Object>> getPageListMap(QueryParam queryParam) {
		if(null != queryParam){
			Page<Comment> commentPage = AR.find(queryParam).page(Comment.class);
			return this.getCommentPageMap(commentPage);
		}
		return null;
	}
	
	private Page<Map<String, Object>> getCommentPageMap(Page<Comment> commentPage){
		
		long totalCount = commentPage.getTotalCount();
		int page = commentPage.getPage();
		int pageSize = commentPage.getPageSize();
		Page<Map<String, Object>> result = new Page<Map<String,Object>>(totalCount, page, pageSize);
		
		List<Comment> comments = commentPage.getResults();
		
		List<Map<String, Object>> nodeMaps = new ArrayList<Map<String,Object>>();
		if(null != comments && comments.size() > 0){
			for(Comment comment : comments){
				Map<String, Object> map = this.getCommentDetail(comment, null);
				if(null != map && !map.isEmpty()){
					nodeMaps.add(map);
				}
			}
		}
		
		result.setResults(nodeMaps);
		
		return result;
	}
	
	private Map<String, Object> getCommentDetail(Comment comment, Long cid) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(null == comment){
			comment = this.getComment(cid);
		}
		if(null != comment){
			
			Long comment_uid = comment.getUid();
			User comment_user = userService.getUser(comment_uid);
			if(null == comment_user){
				return map;
			}

			map.put("cid", comment.getCid());
			map.put("reply_name", comment_user.getLogin_name());
			map.put("reply_avatar", ImageKit.getAvatar(comment_user.getAvatar(), ImageTypes.small));
			
			String content = comment.getContent().replaceAll("\r\n", "<br/>");
			String processed = Processor.process(content);
			map.put("content", processed);
		}
		return map;
	}

	@Override
	public boolean save(Long uid, Long toUid, Long tid, String content) {
		try {
			AR.update("insert into t_comment(uid, to_uid, tid, content, create_time) values(?, ?, ?, ?, ?)",
					uid, toUid, tid, content, DateKit.getCurrentUnixTime()).executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean delete(Long cid) {
		if(null != cid){
			AR.update("delete from t_comment where cid = ?", cid).executeUpdate();
			return true;
		}
		return false;
	}

	@Override
	public Comment getTopicLastComment(Long tid) {
		return AR.find("select * from t_comment where tid = ? order by cid desc", tid).first(Comment.class);
	}
		
}

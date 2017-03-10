package com.javachina.controller;

import com.blade.ioc.annotation.Inject;
import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.mvc.annotation.Controller;
import com.blade.mvc.annotation.PathParam;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.view.ModelAndView;
import com.javachina.Actions;
import com.javachina.Constant;
import com.javachina.Types;
import com.javachina.dto.HomeTopic;
import com.javachina.exception.TipException;
import com.javachina.kit.MapCache;
import com.javachina.kit.SessionKit;
import com.javachina.model.Comment;
import com.javachina.model.LoginUser;
import com.javachina.model.NodeTree;
import com.javachina.model.Topic;
import com.javachina.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Controller
public class TopicController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TopicController.class);

	@Inject
	private TopicService topicService;
	
	@Inject
	private TopicCountService topicCountService;
	
	@Inject
	private NodeService nodeService;
	
	@Inject
	private CommentService commentService;
	
	@Inject
	private SettingsService settingsService;
	
	@Inject
	private FavoriteService favoriteService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private UserlogService userlogService;
	
	@Inject
	private TopicCountService typeCountService;

	private MapCache mapCache = MapCache.single();

	/**
	 * 发布帖子页面
	 */
	@Route(value = "/topic/add", method = HttpMethod.GET)
	public ModelAndView show_add_topic(Request request, Response response){
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			response.go("/");
			return null;
		}
		this.putData(request);
		Long pid = request.queryAsLong("pid");
		request.attribute("pid", pid);
		return this.getView("topic_add");
	}
	
	/**
	 * 编辑帖子页面
	 */
	@Route(value = "/topic/edit/:tid", method = HttpMethod.GET)
	public ModelAndView show_ediot_topic(@PathParam("tid") Integer tid, Request request, Response response){
		
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			response.go("/");
			return null;
		}
		
		Topic topic = topicService.getTopic(tid);
		if(null == topic){
			request.attribute(this.ERROR, "不存在该帖子");
			return this.getView("info");
		}
		
		if(!topic.getUid().equals(user.getUid())){
			request.attribute(this.ERROR, "您无权限编辑该帖");
			return this.getView("info");
		}
		
		// 超过300秒
		if( (DateKit.getCurrentUnixTime() - topic.getCreate_time()) > 300 ){
			request.attribute(this.ERROR, "发帖已经超过300秒，不允许编辑");
			return this.getView("info");
		}
		
		this.putData(request);
		request.attribute("topic", topic);
		
		return this.getView("topic_edit");
	}
	
	/**
	 * 编辑帖子操作
	 */
	@Route(value = "/topic/edit", method = HttpMethod.POST)
	public void edit_topic(Request request, Response response){
		Integer tid = request.queryAsInt("tid");
		String title = request.query("title");
		String content = request.query("content");
		Integer nid = request.queryAsInt("nid");
		
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			this.nosignin(response);
			return;
		}
		
		if(null == tid){
			this.error(response, "不存在该帖子");
			return;
		}
		
		// 不存在该帖子
		Topic topic = topicService.getTopic(tid);
		if(null == topic){
			this.error(response, "不存在该帖子");
			return;
		}
		
		// 无权限操作
		if(!topic.getUid().equals(user.getUid())){
			this.error(response, "无权限操作该帖");
			return;
		}
		
		// 超过300秒
		if( (DateKit.getCurrentUnixTime() - topic.getCreate_time()) > 300 ){
			this.error(response, "超过300秒禁止编辑");
			return;
		}
		
		if(StringKit.isBlank(title) || StringKit.isBlank(content) || null == nid){
			this.error(response, "部分内容未输入");
			return;
		}
		
		if(title.length() < 4 || title.length() > 50){
			this.error(response, "标题长度在4-50个字符哦");
			return;
		}
		
		if(content.length() < 5){
			this.error(response, "您真是一字值千金啊。");
			return;
		}
		
		if(content.length() > 10000){
			this.error(response, "内容太长了，试试少吐点口水");
			return;
		}

		Integer last_time = topicService.getLastUpdateTime(user.getUid());
		if(null != last_time && (DateKit.getCurrentUnixTime() - last_time) < 10 ){
			this.error(response, "您操作频率太快，过一会儿操作吧！");
			return;
		}
		
		try {
			// 编辑帖子
			topicService.update(tid, nid, title, content);
			userlogService.save(user.getUid(), Actions.UPDATE_TOPIC, content);
			
			this.success(response, tid);
		} catch (Exception e) {
			String msg = "编辑帖子失败";
			if (e instanceof TipException) {
				msg = e.getMessage();
			} else {
				LOGGER.error(msg, e);
			}
			this.error(response, msg);
		}
	}
	
	/**
	 * 发布帖子操作
	 */
	@Route(value = "/topic/add", method = HttpMethod.POST)
	public void add_topic(Request request, Response response){
		
		String title = request.query("title");
		String content = request.query("content");
		Integer nid = request.queryAsInt("nid");
		
		LoginUser user = SessionKit.getLoginUser();
		
		if(null == user){
			this.nosignin(response);
			return;
		}
		
		if(StringKit.isBlank(title) || StringKit.isBlank(content) || null == nid){
			this.error(response, "部分内容未输入");
			return;
		}
		
		if(title.length() < 4 || title.length() > 50){
			this.error(response, "标题长度在4-50个字符哦");
			return;
		}
		
		if(content.length() < 5){
			this.error(response, "您真是一字值千金啊。");
			return;
		}
		
		if(content.length() > 10000){
			this.error(response, "内容太长了，试试少吐点口水");
			return;
		}
		
		Integer last_time = topicService.getLastCreateTime(user.getUid());
		if(null != last_time && (DateKit.getCurrentUnixTime() - last_time) < 10 ){
			this.error(response, "您操作频率太快，过一会儿操作吧！");
			return;
		}
		
		// 发布帖子
		try {
			Topic topic = new Topic();
			topic.setUid(user.getUid());
			topic.setNid(nid);
			topic.setTitle(title);
			topic.setContent(content);
			topic.setIs_top(0);
			Integer tid = topicService.save(topic);
			if(null != tid){
				Constant.SYS_INFO = settingsService.getSystemInfo();
				Constant.VIEW_CONTEXT.set("sys_info", Constant.SYS_INFO);
				
				userlogService.save(user.getUid(), Actions.ADD_TOPIC, content);
				this.success(response, tid);
			} else {
				this.error(response, "帖子发布失败");
			}
		} catch (Exception e) {
			String msg = "发布帖子失败";
			if (e instanceof TipException) {
				msg = e.getMessage();
			} else {
				LOGGER.error(msg, e);
			}
			this.error(response, msg);
		}
	}
	
	private void putData(Request request){
		List<NodeTree> nodes = nodeService.getTree();
		request.attribute("nodes", nodes);
	}
	
	/**
	 * 帖子详情页面
	 */
	@Route(value = "/topic/:tid", method = HttpMethod.GET)
	public ModelAndView show_topic(@PathParam("tid") Integer tid, Request request, Response response){
		
		LoginUser user = SessionKit.getLoginUser();

		Integer uid = null;
		if(null != user){
			uid = user.getUid();
		} else {
			SessionKit.setCookie(response, Constant.JC_REFERRER_COOKIE, request.url());
		}
		
		Topic topic = topicService.getTopic(tid);
		if(null == topic || topic.getStatus() != 1){
			response.go("/");
			return null;
		}
		
		this.putDetail(request, uid, topic);

		// 刷新浏览数
		try {
			Integer hits = mapCache.get(Constant.C_TOPIC_VIEWS + ":" + tid);
			if(null == hits){
				hits = 0;
			}
			hits += 1;
			mapCache.set(Constant.C_TOPIC_VIEWS + ":" + tid, hits);
			if(hits >= 10){
				typeCountService.update(Types.views.toString(), tid, 10);
				mapCache.set(Constant.C_TOPIC_VIEWS + ":" + tid, 0);
			}
		} catch (Exception e){
			LOGGER.error("", e);
		}
		return this.getView("topic_detail");
	}
	
	private void putDetail(Request request, Integer uid, Topic topic){
		
		Integer page = request.queryAsInt("p");
		if(null == page || page < 1){
			page = 1;
		}
		
		// 帖子详情
		Map<String, Object> topicMap = topicService.getTopicMap(topic, true);
		request.attribute("topic", topicMap);
		
		// 是否收藏
		boolean is_favorite = favoriteService.isFavorite(Types.topic.toString(), uid, topic.getTid());
		request.attribute("is_favorite", is_favorite);
		
		// 是否点赞
		boolean is_love = favoriteService.isFavorite(Types.love.toString(), uid, topic.getTid());
		request.attribute("is_love", is_love);

		Take cp = new Take(Comment.class);
		cp.and("tid", topic.getTid()).asc("cid").page(page, 20);
		Paginator<Map<String, Object>> commentPage = commentService.getPageListMap(cp);
		request.attribute("commentPage", commentPage);
	}
	
	/**
	 * 评论帖子操作
	 */
	@Route(value = "/comment/add", method = HttpMethod.POST)
	public void add_comment(Request request, Response response){
		
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			this.nosignin(response);
			return;
		}
		
		Integer uid = user.getUid();
		String content = request.query("content");
		Integer tid = request.queryAsInt("tid");
		Topic topic = topicService.getTopic(tid);
		if(null == topic){
			response.go("/");
			return;
		}
		
		if(null == tid || StringKit.isBlank(content)){
			this.error(response, "骚年，有些东西木有填哎！");
			return;
		}
		
		if(content.length() > 5000){
			this.error(response, "内容太长了，试试少吐点口水。");
			return;
		}

		Integer last_time = topicService.getLastUpdateTime(user.getUid());
		if(null != last_time && (DateKit.getCurrentUnixTime() - last_time) < 10 ){
			this.error(response, "您操作频率太快，过一会儿操作吧！");
			return;
		}
		
		// 评论帖子
		try {
			
			String ua = request.userAgent();
			
			boolean flag = topicService.comment(uid, topic.getUid(), tid, content, ua);
			if(flag){
				Constant.SYS_INFO = settingsService.getSystemInfo();
				Constant.VIEW_CONTEXT.set("sys_info", Constant.SYS_INFO);
				
				userlogService.save(user.getUid(), Actions.ADD_COMMENT, content);
				
				this.success(response, "");
			} else {
				this.error(response, "帖子评论失败");
			}
		} catch (Exception e) {
			String msg = "评论帖子失败";
			if (e instanceof TipException) {
				msg = e.getMessage();
			} else {
				LOGGER.error(msg, e);
			}
			this.error(response, msg);
		}
	}
	
	/**
	 * 加精和取消加精
	 */
	@Route(value = "/essence", method = HttpMethod.POST)
	public void essence(Request request, Response response){
		
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			this.nosignin(response);
			return;
		}
		
		if(user.getRole_id() > 3){
			this.error(response, "您无权限操作");
			return;
		}

		Integer tid = request.queryAsInt("tid");
		if(null == tid || tid == 0){
			return;
		}
		
		Topic topic = topicService.getTopic(tid);
		if(null == topic){
			this.error(response, "不存在该帖子");
			return;
		}
		
		try {
			Integer count = topic.getIs_essence() == 1 ? 0 : 1;
			topicService.essence(tid, count);
			userlogService.save(user.getUid(), Actions.ESSENCE, tid+":" + count);
			
			this.success(response, tid);
		} catch (Exception e) {
			String msg = "设置失败";
			if (e instanceof TipException) {
				msg = e.getMessage();
			} else {
				LOGGER.error(msg, e);
			}
			this.error(response, msg);
		}
	}
	
	/**
	 * 帖子下沉
	 */
	@Route(value = "/sink", method = HttpMethod.POST)
	public void sink(Request request, Response response){
		
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			this.nosignin(response);
			return;
		}

		Integer tid = request.queryAsInt("tid");
		if(null == tid || tid == 0){
			return;
		}
		
		try {
			boolean isFavorite = favoriteService.isFavorite(Types.sinks.toString(), user.getUid(), tid);
			if(!isFavorite){
				favoriteService.update(Types.sinks.toString(), user.getUid(), tid);
				topicCountService.update(Types.sinks.toString(), tid, 1);
				topicService.updateWeight(tid);
				userlogService.save(user.getUid(), Actions.SINK, tid+"");
			}
			this.success(response, tid);
		} catch (Exception e) {
			String msg = "设置失败";
			if (e instanceof TipException) {
				msg = e.getMessage();
			} else {
				LOGGER.error(msg, e);
			}
			this.error(response, msg);
		}
	}
	
	/**
	 * 删除帖子
	 */
	@Route(value = "/delete", method = HttpMethod.POST)
	public void delete(Request request, Response response){
		
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			this.nosignin(response);
			return;
		}

		Integer tid = request.queryAsInt("tid");
		if(null == tid || tid == 0 || user.getRole_id() > 2){
			return;
		}
		
		try {
			topicService.delete(tid);
			this.success(response, tid);
		} catch (Exception e) {
			String msg = "删除帖子失败";
			if (e instanceof TipException) {
				msg = e.getMessage();
			} else {
				LOGGER.error(msg, e);
			}
			this.error(response, msg);
		}
	}
	
	/**
	 * 精华帖页面
	 */
	@Route(value = "/essence", method = HttpMethod.GET)
	public ModelAndView essencePage(Request request, Response response){
		// 帖子
		Take tp = new Take(Topic.class);
		Integer page = request.queryInt("p", 1);
		Paginator<HomeTopic> topicPage = topicService.getEssenceTopics(page, 15);
		tp.eq("status", 1).eq("is_essence", 1).desc("create_time", "update_time").page(page, 15);
		request.attribute("topicPage", topicPage);
		
		return this.getView("essence");
	}
	
}

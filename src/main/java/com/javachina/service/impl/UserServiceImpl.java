package com.javachina.service.impl;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.ActiveRecord;
import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.blade.kit.*;
import com.javachina.ImageTypes;
import com.javachina.Types;
import com.javachina.kit.MailKit;
import com.javachina.kit.QiniuKit;
import com.javachina.kit.Utils;
import com.javachina.model.LoginUser;
import com.javachina.model.User;
import com.javachina.model.Userinfo;
import com.javachina.service.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

	@Inject
	private ActiveRecord activeRecord;

	@Inject
	private ActivecodeService activecodeService;
	
	@Inject
	private TopicService topicService;
	
	@Inject
	private UserinfoService userinfoService;
	
	@Inject
	private FavoriteService favoriteService;
	
	@Inject
	private CommentService commentService;
	
	@Inject
	private NoticeService noticeService;
	
	@Override
	public User getUser(Integer uid) {
		if(null == uid){
			return null;
		}
		return activeRecord.byId(User.class, uid);
	}

	@Override
	public User getUser(Take take) {
		if(null == take){
			return null;
		}
		return activeRecord.one(take);
	}

	@Override
	public Paginator<User> getPageList(Take take) {
		if(null != take){
			return activeRecord.page(take);
		}
		return null;
	}
	
	@Override
	public User signup(String loginName, String passWord, String email) throws Exception {
		if(StringKit.isBlank(loginName) || StringKit.isBlank(passWord) || StringKit.isBlank(email)){
			return null;
		}

		if(hasUser(loginName)){
			return null;
		}

		int time = DateKit.getCurrentUnixTime();
		String pwd = EncrypKit.md5(loginName + passWord);
		String avatar = "avatar/default/" + StringKit.getRandomNumber(1, 5) + ".png";

		try {
			User user = new User();
			user.setLogin_name(loginName);
			user.setPass_word(pwd);
			user.setEmail(email);
			user.setAvatar(avatar);
			user.setStatus(0);
			user.setCreate_time(time);
			user.setUpdate_time(time);
			Long uid = activeRecord.insert(user);
			if(null != uid){
				user.setUid(uid.intValue());

				userinfoService.save(user.getUid());

				// 发送邮件通知
				String code = activecodeService.save(user, Types.signup.toString());

				//  发送注册邮件
				MailKit.sendSignup(user.getLogin_name(), user.getEmail(), code);
			}
			return user;
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public boolean delete(Integer uid) {
		if(null != uid){
			activeRecord.delete(User.class, uid);
			return true;
		}
		return false;
	}

	@Override
	public boolean resetPwd(String email) {
		
		return false;
	}

	@Override
	public User signin(String loginName, String passWord) {
		if(StringKit.isBlank(loginName) || StringKit.isBlank(passWord)){
			return null;
		}

		String pwd = EncrypKit.md5(loginName + passWord);

		Take take = new Take(User.class);
		take.eq("pass_word", pwd)
			.in("status", 0, 1)
			.eq("login_name", loginName)
			.or("email", "=", loginName);

		return activeRecord.one(take);
	}

	@Override
	public Map<String, Object> getUserDetail(Integer uid) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(null != uid){

			User user = activeRecord.byId(User.class, uid);
			Userinfo userinfo = activeRecord.byId(Userinfo.class, uid);

			map = BeanKit.beanToMap(userinfo);
			map.put("username", user.getLogin_name());
			map.put("uid", uid);
			map.put("email", user.getEmail());
			map.put("avatar", user.getAvatar());
			map.put("create_time", user.getCreate_time());
			String avatar = Utils.getAvatar(user.getAvatar(), ImageTypes.normal);
			map.put("avatar", avatar);
		}
		return map;
	}
	
	@Override
	public boolean updateStatus(Integer uid, Integer status) {
		if(null != uid && null != status){
			User temp = new User();
			temp.setUid(uid);
			temp.setStatus(status);
			activeRecord.update(temp);
		}
		return false;
	}

	@Override
	public boolean updateAvatar(Integer uid, String avatar_path) {
		try {
			if(null == uid || StringKit.isBlank(avatar_path)){
				return false;
			}
			
			File file = new File(avatar_path);
			if(file.exists()){
				
				User user = new User();
				user.setUid(uid);

				String ext = FileKit.getExtension(file.getName());
				if(StringKit.isBlank(ext)){
					ext = "png";
				}
				
				String key = "avatar/" + user.getLogin_name() + "/" + StringKit.getRandomChar(4) + "/" + StringKit.getRandomNumber(4) + "." + ext;
				
				boolean flag = QiniuKit.upload(file, key);
				if(flag){
					user.setAvatar(key);
					activeRecord.update(user);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean updatePwd(Integer uid, String newpwd) {
		try {
			if(null == uid || StringKit.isBlank(newpwd)){
				return false;
			}
			User user = new User();
			user.setUid(uid);
			user.setPass_word(newpwd);
			activeRecord.update(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public LoginUser getLoginUser(User user, Integer uid) {
		if(null == user){
			user = this.getUser(uid);
		}
		if(null != user){
			LoginUser loginUser = new LoginUser();
			loginUser.setUid(user.getUid());
			loginUser.setUser_name(user.getLogin_name());
			loginUser.setPass_word(user.getPass_word());
			loginUser.setStatus(user.getStatus());
			loginUser.setRole_id(user.getRole_id());
			String avatar = Utils.getAvatar(user.getAvatar(), ImageTypes.normal);
			loginUser.setAvatar(avatar);

			Integer comments = commentService.getComments(user.getUid());
			loginUser.setComments(comments);

			Integer topics = topicService.getTopics(user.getUid());
			loginUser.setTopics(topics);

			Integer notices = noticeService.getNotices(user.getUid());
			loginUser.setNotices(notices);
			
			Userinfo userinfo = userinfoService.getUserinfo(user.getUid());
			if(null != userinfo){
				loginUser.setJobs(userinfo.getJobs());
				loginUser.setNick_name(userinfo.getNick_name());
			}
			
			Integer my_topics = favoriteService.favorites(Types.topic.toString(), user.getUid());
			Integer my_nodes = favoriteService.favorites(Types.node.toString(), user.getUid());
			
			loginUser.setMy_topics(my_topics);
			loginUser.setMy_nodes(my_nodes);

			Integer following = favoriteService.favorites(Types.following.toString(), user.getUid());
			loginUser.setFollowing(following);
			
			return loginUser;
		}
		return null;
	}

	@Override
	public boolean hasUser(String login_name) {
		if(StringKit.isNotBlank(login_name)){
			Take take = new Take(User.class);
			take.in("status", 0, 1)
					.eq("login_name", login_name)
					.or("email", "=", login_name);

			return activeRecord.count(take) > 0;
		}
		return false;
	}

	@Override
	public User getUserByLoginName(String user_name) {
		if(StringKit.isNotBlank(user_name)){
			Take take = new Take(User.class);
			take.in("status", 0, 1)
				.eq("login_name", user_name)
				.or("email", "=", user_name);

			return activeRecord.one(take);
		}
		return null;
	}

	@Override
	public boolean updateRole(Integer uid, Integer role_id) {
		try {
			if(null == uid || null == role_id || role_id == 1){
				return false;
			}
			User temp = new User();
			temp.setUid(uid);
			temp.setRole_id(role_id);
			activeRecord.update(temp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}

package com.javachina.service;

import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.javachina.model.LoginUser;
import com.javachina.model.User;

import java.util.Map;

public interface UserService {
	
	User getUser(Integer uid);

	User getUser(Take take);
	
	User getUserByLoginName(String user_name);
	
	Map<String, Object> getUserDetail(Integer uid);
	
	Paginator<User> getPageList(Take take);
	
	User signup(String loginName, String passWord, String email) throws Exception;
	
	User signin(String loginName, String passWord);
	
	LoginUser getLoginUser(User user, Integer uid);
	
	boolean hasUser(String login_name);
	
	boolean delete(Integer uid);
	
	boolean updateStatus(Integer uid, Integer status);
	
	boolean resetPwd(String email);

	boolean updateAvatar(Integer uid, String avatar_path);

	boolean updatePwd(Integer uid, String new_pwd);

	boolean updateRole(Integer uid, Integer role_id);
	
}

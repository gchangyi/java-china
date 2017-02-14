package com.javachina.service;

import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.javachina.model.Userinfo;

public interface UserinfoService {
	
	Userinfo getUserinfo(Integer uid);
	
	Paginator<Userinfo> getPageList(Take queryParam);
	
	boolean save(Integer uid);
	
	boolean update(Integer uid, String nickName, String jobs, String webSite, String github, String weibo, String location, String signature, String instructions );
	
	boolean delete(Integer uid);
		
}

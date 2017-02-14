package com.javachina.service.impl;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.ActiveRecord;
import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.javachina.config.DBConfig;
import com.javachina.model.Userinfo;
import com.javachina.service.UserinfoService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserinfoServiceImpl implements UserinfoService {

	@Inject
	private ActiveRecord activeRecord;

	@Override
	public Userinfo getUserinfo(Integer uid) {
		return activeRecord.byId(Userinfo.class, uid);
	}
		
	private List<Userinfo> getUserinfoList(Take queryParam) {
		if(null != queryParam){
			return activeRecord.list(queryParam);
		}
		return null;
	}
	
	@Override
	public Paginator<Userinfo> getPageList(Take queryParam) {
		if(null != queryParam){
			return activeRecord.page(queryParam);
		}
		return null;
	}
	
	@Override
	public boolean save(Integer uid) {
		if(null == uid){
			return false;
		}
		try {
			Userinfo userinfo = new Userinfo();
			userinfo.setUid(uid);
			activeRecord.insert(userinfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean update(Integer uid, String nickName, String jobs, String webSite,
			String github, String weibo, String location, String signature, String instructions) {
		if(null != uid){

			Userinfo userinfo = new Userinfo();
			userinfo.setUid(uid);

			List<Object> params = new ArrayList<Object>();
			if(null != nickName){
				userinfo.setNick_name(nickName);
			}
			if(null != jobs){
				userinfo.setJobs(jobs);
			}
			if(null != webSite){
				userinfo.setWeb_site(webSite);
			}
			if(null != github){
				userinfo.setGithub(github);
			}
			if(null != weibo){
				userinfo.setWeibo(weibo);
			}
			if(null != location){
				userinfo.setLocation(location);
			}
			if(null != signature){
				userinfo.setSignature(signature);
			}
			if(null != instructions){
				userinfo.setInstructions(instructions);
			}
			return activeRecord.update(userinfo) > 0;
		}
		return false;
	}
	
	@Override
	public boolean delete(Integer uid) {
		if(null != uid){
			return activeRecord.delete(Userinfo.class, uid) > 0;
		}
		return false;
	}

}

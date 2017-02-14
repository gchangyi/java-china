package com.javachina.service.impl;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.ActiveRecord;
import com.blade.kit.DateKit;
import com.javachina.config.DBConfig;
import com.javachina.model.Link;
import com.javachina.service.LinkService;

@Service
public class LinkServiceImpl implements LinkService {

	@Inject
	private ActiveRecord activeRecord;
	
	@Override
	public boolean save(String title, String url) {
		Link link = new Link();
		link.setTitle(title);
		link.setUrl(url);
		link.setCreate_time(DateKit.getCurrentUnixTime());
		activeRecord.insert(link);
		return false;
	}
	
	@Override
	public boolean delete(Integer id) {
		if(null != id){
			return activeRecord.delete(Link.class, id) > 0;
		}
		return false;
	}
		
}

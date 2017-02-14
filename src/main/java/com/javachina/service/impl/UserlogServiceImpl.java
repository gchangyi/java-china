package com.javachina.service.impl;

import com.blade.context.WebContextHolder;
import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.ActiveRecord;
import com.blade.kit.DateKit;
import com.javachina.config.DBConfig;
import com.javachina.kit.Utils;
import com.javachina.model.Userlog;
import com.javachina.service.UserlogService;

@Service
public class UserlogServiceImpl implements UserlogService {

	@Inject
	private ActiveRecord activeRecord;

	@Override
	public void save(final Integer uid, final String action, final String content) {
		final String ip = Utils.getIpAddr(WebContextHolder.me().getRequest());
		Utils.run( () -> {
			Userlog userlog = new Userlog();
			userlog.setUid(uid);
			userlog.setAction(action);
			userlog.setContent(content);
			userlog.setIp_addr(ip);
			userlog.setCreate_time(DateKit.getCurrentUnixTime());
			activeRecord.insert(userlog);
		} );
	}
	
}
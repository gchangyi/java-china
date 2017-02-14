package com.javachina.service;

import com.javachina.model.Activecode;
import com.javachina.model.User;

public interface ActivecodeService {
	
	Activecode getActivecode(String code);
	
	String save(User user, String type) throws Exception;
	
	boolean useCode(String code) throws Exception;

	boolean resend(Integer uid) throws Exception;
	
}

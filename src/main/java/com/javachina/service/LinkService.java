package com.javachina.service;

public interface LinkService {
	
	boolean save( String title, String url);
	
	boolean delete(Integer id);
		
}

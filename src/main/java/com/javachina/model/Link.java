package com.javachina.model;

import com.blade.jdbc.annotation.Table;

import java.io.Serializable;

/**
 * Link对象
 */
@Table(name = "t_link", pk = "id")
public class Link implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String title;
	
	private String url;
	
	private Integer create_time;
	
	public Link(){}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}
	
}
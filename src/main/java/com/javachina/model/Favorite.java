package com.javachina.model;

import com.blade.jdbc.annotation.Table;

import java.io.Serializable;

/**
 * Favorite对象
 */
@Table(name = "t_favorite", pk = "id")
public class Favorite implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	//topic:帖子 node:节点
	private String type;
	
	private Integer uid;
	
	private Integer event_id;
	
	private Integer create_time;
	
	public Favorite(){}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}
	
	public Integer getEvent_id() {
		return event_id;
	}

	public void setEvent_id(Integer event_id) {
		this.event_id = event_id;
	}
	
	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}
	
}
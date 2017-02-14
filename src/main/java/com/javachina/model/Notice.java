package com.javachina.model;

import com.blade.jdbc.annotation.Table;

import java.io.Serializable;

/**
 * Notice对象
 */
@Table(name = "t_notice", pk = "id")
public class Notice implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;

	private Integer uid;

	private Integer to_uid;
	
	private Integer event_id;
	
	private String type;
	
	private Boolean is_read;
	
	private Integer create_time;
	
	public Notice(){}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTo_uid() {
		return to_uid;
	}

	public void setTo_uid(Integer to_uid) {
		this.to_uid = to_uid;
	}

	public Integer getEvent_id() {
		return event_id;
	}

	public void setEvent_id(Integer event_id) {
		this.event_id = event_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIs_read() {
		return is_read;
	}

	public void setIs_read(Boolean is_read) {
		this.is_read = is_read;
	}

	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}
}
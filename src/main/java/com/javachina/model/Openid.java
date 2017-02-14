package com.javachina.model;

import java.io.Serializable;
import java.util.Date;
import com.blade.jdbc.annotation.Table;

//
@Table(name = "t_openid", pk = "id")
public class Openid implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String type;

	private Integer open_id;

	private Integer uid;

	private Integer create_time;

	public Openid(){}

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

	public Integer getOpen_id() {
		return open_id;
	}

	public void setOpen_id(Integer open_id) {
		this.open_id = open_id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}


}
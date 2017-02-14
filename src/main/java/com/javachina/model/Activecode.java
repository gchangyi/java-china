package com.javachina.model;

import com.blade.jdbc.annotation.Table;

import java.io.Serializable;

/**
 * Activecode对象
 */
@Table(name = "t_activecode")
public class Activecode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private Integer uid;
	
	private String code;
	
	private String type;
	
	private Integer is_use;
	
	//过期时间
	private Integer expires_time;
	
	//创建时间
	private Integer create_time;
	
	public Activecode(){}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public Integer getIs_use() {
		return is_use;
	}

	public void setIs_use(Integer is_use) {
		this.is_use = is_use;
	}
	
	public Integer getExpires_time() {
		return expires_time;
	}

	public void setExpires_time(Integer expires_time) {
		this.expires_time = expires_time;
	}
	
	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
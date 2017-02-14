package com.javachina.model;

import com.blade.jdbc.annotation.Table;

import java.io.Serializable;

/**
 * Love对象
 */
@Table(name = "t_love", pk = "id")
public class Love implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private Integer tid;
	
	private Integer uid;
	
	public Love(){}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}
	
}
package com.javachina.model;

import java.io.Serializable;

import com.blade.jdbc.annotation.Table;

/**
 * Comment对象
 */
@Table(name = "t_comment", pk = "cid")
public class Comment implements Serializable {

	private static final Long serialVersionUID = 1L;
	
	private Integer cid;
	
	//评论人uid
	private Integer uid;
	
	//被评论人uid
	private Integer to_uid;
	
	//帖子id
	private Integer tid;
	
	//评论内容
	private String content;
	
	//设备
	private String device;
	
	//评论时间
	private Integer create_time;
	
	public Comment(){}
	
	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}
	
	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}
	
	public Integer getTo_uid() {
		return to_uid;
	}

	public void setTo_uid(Integer to_uid) {
		this.to_uid = to_uid;
	}
	
	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}
	
}
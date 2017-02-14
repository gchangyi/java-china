package com.javachina.model;

import com.blade.jdbc.annotation.Table;

import java.io.Serializable;

/**
 * Topic对象
 */
@Table(name = "t_topic", pk = "tid")
public class Topic implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer tid;
	
	//发布人
	private Integer uid;
	
	//所属节点
	private Integer nid;
	
	//帖子标题
	private String title;
	
	//帖子内容
	private String content;
	
	//是否置顶
	private Integer is_top;
	
	//是否是精华贴
	private Integer is_essence;
	
	// 帖子权重
	private Double weight;
	
	//帖子创建时间
	private Integer create_time;
	
	//最后更新时间
	private Integer update_time;
	
	//1:正常 2:删除
	private Integer status;
	
	public Topic(){}
	
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
	
	public Integer getNid() {
		return nid;
	}

	public void setNid(Integer nid) {
		this.nid = nid;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getIs_top() {
		return is_top;
	}

	public void setIs_top(Integer is_top) {
		this.is_top = is_top;
	}
	
	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}
	
	public Integer getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Integer update_time) {
		this.update_time = update_time;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getIs_essence() {
		return is_essence;
	}

	public void setIs_essence(Integer is_essence) {
		this.is_essence = is_essence;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
}
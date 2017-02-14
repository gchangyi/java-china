package com.javachina.model;

import com.blade.jdbc.annotation.Table;

import java.io.Serializable;

@Table(name = "t_topiccount", pk = "tid")
public class TopicCount implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer tid;
	private Integer views;
	private Integer loves;
	private Integer favorites;
	private Integer sinks;
	private Integer comments;
	private Integer create_time;
	
	public TopicCount() {
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	public Integer getLoves() {
		return loves;
	}

	public void setLoves(Integer loves) {
		this.loves = loves;
	}

	public Integer getFavorites() {
		return favorites;
	}

	public void setFavorites(Integer favorites) {
		this.favorites = favorites;
	}

	public Integer getComments() {
		return comments;
	}

	public void setComments(Integer comments) {
		this.comments = comments;
	}

	public Integer getSinks() {
		return sinks;
	}

	public void setSinks(Integer sinks) {
		this.sinks = sinks;
	}

	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}
	
}
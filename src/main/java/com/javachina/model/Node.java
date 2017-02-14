package com.javachina.model;

import com.blade.jdbc.annotation.Table;

import java.io.Serializable;

/**
 * Node对象
 */
@Table(name = "t_node", pk = "nid")
public class Node implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer nid;
	
	//父节点id
	private Integer pid;
	
	//节点名称
	private String title;
	
	//节点描述
	private String description;
	
	//节点英文简写
	private String slug;
	
	// 节点图片
	private String pic;
	
	//帖子数
	private Integer topics;
	
	//创建时间
	private Integer create_time;
	
	//最后更新时间
	private Integer update_time;
	
	//是否删除
	private Integer is_del;
	
	public Node(){}
	
	public Integer getNid() {
		return nid;
	}

	public void setNid(Integer nid) {
		this.nid = nid;
	}
	
	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
	
	public Integer getTopics() {
		return topics;
	}

	public void setTopics(Integer topics) {
		this.topics = topics;
	}
	
	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}
	
	public Integer getIs_del() {
		return is_del;
	}

	public void setIs_del(Integer is_del) {
		this.is_del = is_del;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public Integer getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Integer update_time) {
		this.update_time = update_time;
	}
	
}
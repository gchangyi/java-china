package com.javachina.model;

import com.blade.jdbc.annotation.Table;

import java.io.Serializable;

/**
 * User对象
 */
@Table(name = "t_user", pk = "uid")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer uid;
	
	private String login_name;
	
	private String pass_word;
	
	//头像
	private String avatar;
	
	//电子邮箱
	private String email;
	
	//创建时间
	private Integer create_time;
	
	//最后一次操作时间
	private Integer update_time;
	
	//5:普通用户 2:管理员 1:系统管理员
	private Integer role_id;
	
	//0:待激活 1:正常 2：删除
	private Integer status;
	
	public User(){}
	
	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}
	
	public String getLogin_name() {
		return login_name;
	}

	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}
	
	public String getPass_word() {
		return pass_word;
	}

	public void setPass_word(String pass_word) {
		this.pass_word = pass_word;
	}
	
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
	
	public Integer getRole_id() {
		return role_id;
	}

	public void setRole_id(Integer role_id) {
		this.role_id = role_id;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "User [uid=" + uid + ", login_name=" + login_name + ", pass_word=" + pass_word + ", avatar=" + avatar
				+ ", email=" + email + ", create_time=" + create_time + ", update_time=" + update_time + ", role_id="
				+ role_id + ", status=" + status + "]";
	}

}
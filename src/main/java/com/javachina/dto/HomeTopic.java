package com.javachina.dto;

import java.io.Serializable;

/**
 * Created by biezhi on 2017/2/12.
 */
public class HomeTopic implements Serializable {

    private Integer tid;
    private String login_name;
    private String avatar;
    private String title;
    private Integer create_time;
    private Integer update_time;
    private String node_title;
    private String node_slug;
    private int comments;

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public String getLogin_name() {
        return login_name;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Integer create_time) {
        this.create_time = create_time;
    }

    public String getNode_title() {
        return node_title;
    }

    public void setNode_title(String node_title) {
        this.node_title = node_title;
    }

    public String getNode_slug() {
        return node_slug;
    }

    public void setNode_slug(String node_slug) {
        this.node_slug = node_slug;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public Integer getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Integer update_time) {
        this.update_time = update_time;
    }
}

package com.smartmax.hrms.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;


@Entity
public class UserImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id",nullable=false, updatable=false)
	private int id;
	private String name;
	@Column(name="mime_type")
	private String mimeType;
	private long size =0;
	public UserImage(int id, String name, String mimeType, long size) {
		super();
		this.id = id;
		this.name = name;
		this.mimeType = mimeType;
		this.size = size;
	}
	public UserImage(String name){
		this.name = name;
	}
	public UserImage() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
//	public User getUser() {
//		return user;
//	}
//	public void setUser(User user) {
//		this.user =user;
//	}
//	
}

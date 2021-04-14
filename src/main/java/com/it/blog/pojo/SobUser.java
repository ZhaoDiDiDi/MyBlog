package com.it.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table ( name ="tb_user" )
@Getter
@Setter
public class SobUser {

	@Id
	private String id;
  	@Column(name = "user_name" )
	private String userName;
  	@Column(name = "password" )
	private String password;
  	@Column(name = "roles" )
	private String roles;
  	@Column(name = "avatar" )
	private String avatar;
  	@Column(name = "email" )
	private String email;
  	@Column(name = "sign" )
	private String sign;
  	@Column(name = "state" )
	private String state;
  	@Column(name = "reg_ip" )
	private String regIp;
	@Column(name = "login_ip" )
	private String loginIp;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "create_time" )
	private Date createTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "update_time" )
	private Date updateTime;

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}
}

package com.it.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table ( name ="tb_friends" )
@Getter
@Setter
public class FriendLink {

  	@Id
	private String id;
  	@Column(name = "name" )
	private String name;
  	@Column(name = "logo" )
	private String logo;
  	@Column(name = "url" )
	private String url;
  	@Column(name = "`order`" )
	private long order = 1l;
  	@Column(name = "state" )
	private String state = "1";
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "create_time" )
	private Date createTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "update_time" )
	private Date updateTime;
}

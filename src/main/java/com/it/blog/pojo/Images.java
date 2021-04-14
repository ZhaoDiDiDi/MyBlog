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
@Table ( name ="tb_images" )
@Getter
@Setter
public class Images {

  	@Id
	private String id;
  	@Column(name = "user_id" )
	private String userId;
  	@Column(name = "url" )
	private String url;
  	@Column(name = "path" )
	private String path;
  	@Column(name = "content_type" )
	private String contentType;
  	@Column(name = "name" )
	private String name;
  	@Column(name = "state" )
	private String state;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "create_time" )
	private Date createTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "update_time" )
	private Date updateTime;
}

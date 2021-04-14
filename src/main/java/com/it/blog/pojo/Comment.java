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
@Table ( name ="tb_comment" )
@Getter
@Setter
public class Comment {

  	@Id
	private String id;
  	@Column(name = "parent_content" )
	private String parentContent;
  	@Column(name = "article_id" )
	private String articleId;
  	@Column(name = "content" )
	private String content;
  	@Column(name = "user_id" )
	private String userId;
  	@Column(name = "user_avatar" )
	private String userAvatar;
  	@Column(name = "user_name" )
	private String userName;
  	@Column(name = "state" )
	private String state = "1";
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "create_time" )
	private Date createTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "update_time" )
	private Date updateTime;
}

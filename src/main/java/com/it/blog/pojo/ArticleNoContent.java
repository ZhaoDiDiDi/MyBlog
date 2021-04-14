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
@Table ( name ="tb_article" )
@Getter
@Setter
public class ArticleNoContent {

  	@Id
	private String id;
  	@Column(name = "title" )
	private String title;
  	@Column(name = "user_id" )
	private String userId;
  	@Column(name = "category_id" )
	private String categoryId;
	//类型（0表示富文本，1表示markdown）
	@Column(name = "type" )
	private String type;
	@Column(name = "state" )
	//0表示删除、1表示已经发布、2表示草稿、3表示置顶
	private String state = "1";
  	@Column(name = "summary" )
	private String summary;
  	@Column(name = "labels" )
	private String labels;
  	@Column(name = "view_count" )
	private long viewCount = 0l;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "create_time" )
	private Date createTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "update_time" )
	private Date updateTime;
	@Column(name = "cover")
	private String cover;
}

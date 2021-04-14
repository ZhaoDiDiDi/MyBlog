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
@Table ( name ="tb_settings" )
@Setter
@Getter
public class Setting {

  	@Id
	private String id;
  	@Column(name = "`key`" )
	private String key;
  	@Column(name = "`value`" )  //防止sql关键字冲突
	private String value = "1";
  	@Column(name = "create_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
  	@Column(name = "update_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
}

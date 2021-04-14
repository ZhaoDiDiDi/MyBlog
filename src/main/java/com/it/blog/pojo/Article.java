package com.it.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tb_article")
@Getter
@Setter
public class Article {

	@Id
	private String id;
	@Column(name = "title")
	private String title;
	@Column(name = "user_id")
	private String userId;
	@Column(name = "category_id")
	private String categoryId;
	@Column(name = "content")
	private String content;
	//类型（0表示富文本，1表示markdown）
	@Column(name = "type")
	private String type;
	@Column(name = "state")
	//0表示删除、1表示已经发布、2表示草稿、3表示置顶
	private String state = "1";
	@Column(name = "summary")
	private String summary;
	@Column(name = "labels")
	private String label;
	@Column(name = "view_count")
	private long viewCount = 0l;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "create_time" )
	private Date createTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "update_time" )
	private Date updateTime;
	@Column(name = "cover")
	private String cover;

	@OneToOne(targetEntity = SobUser.class)
	@JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
	private SobUser SobUser;

	@Transient
	private List<String> labels = new ArrayList<>();

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public String getLabel() {
		//打散到集合里
		if (this.label != null) {
			this.labels.clear();//先清空
			if (!this.label.contains("-")) {
				this.labels.add(this.label);
			} else {
				String[] split = this.label.split("-");
				List<String> strings = Arrays.asList(split);
				this.labels.addAll(strings);
			}
		}
		return label;
	}
}

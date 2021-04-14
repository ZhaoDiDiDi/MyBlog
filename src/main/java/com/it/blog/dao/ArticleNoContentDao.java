package com.it.blog.dao;

import com.it.blog.pojo.Article;
import com.it.blog.pojo.ArticleNoContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArticleNoContentDao extends JpaRepository<ArticleNoContent, String>, JpaSpecificationExecutor<ArticleNoContent> {
    Article findOneById(String articleId);
}

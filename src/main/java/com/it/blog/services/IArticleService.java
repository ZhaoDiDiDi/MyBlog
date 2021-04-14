package com.it.blog.services;

import com.it.blog.pojo.Article;
import com.it.blog.response.ResponseResult;

public interface IArticleService {
    ResponseResult postArticle(Article article);

    ResponseResult listArticles(int page, int size, String keyword, String categoryId, String state);

    ResponseResult getArticleById(String articleId);

    ResponseResult updateArticle(String articleId, Article article);

    ResponseResult deleteArticleById(String articleId);

    ResponseResult deleteArticleByState(String articleId);

    ResponseResult topArticle(String articleId);

    ResponseResult listTopArticles();
}

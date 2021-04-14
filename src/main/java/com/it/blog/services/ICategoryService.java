package com.it.blog.services;

import com.it.blog.pojo.Category;
import com.it.blog.response.ResponseResult;

public interface ICategoryService {
    ResponseResult addCategory(Category category);

    ResponseResult getCategory(String categoryId);

    ResponseResult listCategories();

    ResponseResult updateCategory(String categoryId, Category category);

    ResponseResult deleteCategory(String categoryId);
}

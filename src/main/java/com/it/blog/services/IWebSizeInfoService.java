package com.it.blog.services;

import com.it.blog.response.ResponseResult;

public interface IWebSizeInfoService {
    ResponseResult getWebSizeTitle();

    ResponseResult getSizeViewCount();

    ResponseResult getSeoInfo();

    ResponseResult putWebSizeTitle(String title);

    ResponseResult putSeoInfo(String keywords, String description);
}

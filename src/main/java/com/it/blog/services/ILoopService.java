package com.it.blog.services;

import com.it.blog.pojo.Looper;
import com.it.blog.response.ResponseResult;

public interface ILoopService {

    ResponseResult addLoop(Looper looper);

    ResponseResult deleteLoop(String loopId);

    ResponseResult updateLoop(String loopId, Looper looper);

    ResponseResult getLoop(String loopId);

    ResponseResult listLoop();
}

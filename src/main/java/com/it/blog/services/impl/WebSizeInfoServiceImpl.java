package com.it.blog.services.impl;

import com.it.blog.dao.SettingDao;
import com.it.blog.pojo.Setting;
import com.it.blog.response.ResponseResult;
import com.it.blog.services.IWebSizeInfoService;
import com.it.blog.utils.Constants;
import com.it.blog.utils.IdWorker;
import com.it.blog.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;

@Service
@Transactional
@Slf4j
public class WebSizeInfoServiceImpl implements IWebSizeInfoService {

    @Autowired
    private SettingDao settingDao;

    @Autowired
    private IdWorker idWorker;

    @Override
    public ResponseResult getWebSizeTitle() {
        Setting title = settingDao.findOneByKey(Constants.Settings.WEB_SIZE_TITLE);
        return ResponseResult.SUCCESS("获取网站title成功").setData(title);
    }

    @Override
    public ResponseResult getSizeViewCount() {
        Setting descriptionFromDb = settingDao.findOneByKey(Constants.Settings.WEB_SIZE_COUNT);
        if (descriptionFromDb == null) {
            descriptionFromDb = new Setting();
            descriptionFromDb.setId(idWorker.nextId() + "");
            descriptionFromDb.setKey(Constants.Settings.WEB_SIZE_COUNT);
            descriptionFromDb.setUpdateTime(new Date());
            descriptionFromDb.setCreateTime(new Date());
        }
        HashMap<String, Integer> result = new HashMap<>();
        result.put(descriptionFromDb.getKey(), Integer.valueOf(descriptionFromDb.getValue()));
        return ResponseResult.SUCCESS("获取网站浏览量成功.").setData(result);
    }

    /**
     * 这个是全网站的访问量，要做得细一点，还得分来源
     * 这里只统计浏览量，只统计文章的浏览量，提供一个浏览量的统计接口（页面级的）
     *
     * @return 浏览量
     */
    @Override
    public ResponseResult getSeoInfo() {
        Setting description = settingDao.findOneByKey(Constants.Settings.WEB_SIZE_DESCRIPTION);
        Setting keywords = settingDao.findOneByKey(Constants.Settings.WEB_SIZE_KEYWORDS);
        HashMap<String, String> result = new HashMap<>();
        result.put(description.getKey(), description.getValue());
        result.put(keywords.getKey(), keywords.getValue());
        return ResponseResult.SUCCESS("获取SEO信息成功").setData(result);
    }

    @Override
    public ResponseResult putWebSizeTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("网站标题不可以为空.");
        }
        Setting titleFromDb = settingDao.findOneByKey(Constants.Settings.WEB_SIZE_TITLE);
        if (titleFromDb == null) {
            titleFromDb = new Setting();
            titleFromDb.setId(idWorker.nextId() + "");
            titleFromDb.setUpdateTime(new Date());
            titleFromDb.setCreateTime(new Date());
            titleFromDb.setKey(Constants.Settings.WEB_SIZE_TITLE);
        }
        titleFromDb.setValue(title);
        settingDao.save(titleFromDb);
        return ResponseResult.SUCCESS("网站Title更新成功.");
    }

    @Override
    public ResponseResult putSeoInfo(String keywords, String description) {
        //判断
        if (TextUtils.isEmpty(keywords)) {
            return ResponseResult.FAILED("关键字不可以为空.");
        }
        if (TextUtils.isEmpty(description)) {
            return ResponseResult.FAILED("网站描述不可以为空.");
        }
        Setting descriptionFromDb = settingDao.findOneByKey(Constants.Settings.WEB_SIZE_DESCRIPTION);
        if (descriptionFromDb == null) {
            descriptionFromDb = new Setting();
            descriptionFromDb.setId(idWorker.nextId() + "");
            descriptionFromDb.setCreateTime(new Date());
            descriptionFromDb.setUpdateTime(new Date());
            descriptionFromDb.setKey(Constants.Settings.WEB_SIZE_DESCRIPTION);
        }
        descriptionFromDb.setValue(description);
        settingDao.save(descriptionFromDb);
        Setting keyWordsFromDb = settingDao.findOneByKey(Constants.Settings.WEB_SIZE_KEYWORDS);
        if (keyWordsFromDb == null) {
            keyWordsFromDb = new Setting();
            keyWordsFromDb.setId(idWorker.nextId() + "");
            keyWordsFromDb.setCreateTime(new Date());
            keyWordsFromDb.setUpdateTime(new Date());
            keyWordsFromDb.setKey(Constants.Settings.WEB_SIZE_KEYWORDS);
        }
        keyWordsFromDb.setValue(keywords);
        settingDao.save(keyWordsFromDb);
        return ResponseResult.SUCCESS("更新SEO信息成功.");
    }
}

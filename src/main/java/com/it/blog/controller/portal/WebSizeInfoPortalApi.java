package com.it.blog.controller.portal;

import com.it.blog.response.ResponseResult;
import com.it.blog.services.ICategoryService;
import com.it.blog.services.IFriendLinkService;
import com.it.blog.services.ILoopService;
import com.it.blog.services.IWebSizeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/web_size_info")
public class WebSizeInfoPortalApi {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ILoopService loopService;

    @Autowired
    private IWebSizeInfoService webSizeInfoService;

    @Autowired
    private IFriendLinkService friendLinkService;

    @GetMapping("/categories")
    public ResponseResult getCategories() {
        return categoryService.listCategories();
    }

    @GetMapping("/title")
    public ResponseResult getWebSizeTitle() {
        return webSizeInfoService.getWebSizeTitle();
    }

    @GetMapping("/view_count")
    public ResponseResult getWebSizeCount() {
        return webSizeInfoService.getSizeViewCount();
    }

    @GetMapping("/seo")
    public ResponseResult getWebSizeInfo() {
        return webSizeInfoService.getSeoInfo();
    }

    @GetMapping("/loop")
    public ResponseResult getLoops() {
        return loopService.listLoop();
    }

    @GetMapping("/friend_link")
    public ResponseResult getLinks() {
        return friendLinkService.listFriendLinks();
    }
}

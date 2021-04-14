package com.it.blog.controller.admin;

import com.it.blog.response.ResponseResult;
import com.it.blog.services.IWebSizeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/web_size_info")
public class WebSizeInfoAdminApi {

    @Autowired
    private IWebSizeInfoService iWebSizeInfoService;

    //@PreAuthorize("@permission.admin()")
    @GetMapping("/title")
    public ResponseResult getWebSizeTitle() {
        return iWebSizeInfoService.getWebSizeTitle();
    }

    //@CheckTooFrequentCommit
    //@PreAuthorize("@permission.admin()")
    @PutMapping("/title")
    public ResponseResult upWebSizeTitle(@RequestParam("title") String title) {
        return iWebSizeInfoService.putWebSizeTitle(title);
    }

    //@PreAuthorize("@permission.admin()")
    @GetMapping("/seo")
    public ResponseResult getSeoInfo() {
        return iWebSizeInfoService.getSeoInfo();
    }

    //@CheckTooFrequentCommit
    //@PreAuthorize("@permission.admin()")
    @PutMapping("/seo")
    public ResponseResult putSeoInfo(@RequestParam("keywords") String keywords,
                                     @RequestParam("description") String description) {
        return iWebSizeInfoService.putSeoInfo(keywords, description);
    }

    //@PreAuthorize("@permission.admin()")
    @GetMapping("/view_count")
    public ResponseResult getWebSizeViewCount() {
        return iWebSizeInfoService.getSizeViewCount();
    }
}
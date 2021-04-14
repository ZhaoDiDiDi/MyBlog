package com.it.blog.controller.admin;

import com.it.blog.pojo.Looper;
import com.it.blog.response.ResponseResult;
import com.it.blog.services.ILoopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/loop")
public class LooperAdminApi {

    @Autowired
    private ILoopService loopService;

    @PreAuthorize("permission.admin()")
    @PostMapping
    public ResponseResult addLoop(@RequestBody Looper looper) {
        return loopService.addLoop(looper);
    }

    @PreAuthorize("permission.admin()")
    @PostMapping("/{loopId}")
    public ResponseResult deleteLoop(@PathVariable("loopId") String loopId) {
        return loopService.deleteLoop(loopId);
    }

    @PreAuthorize("@permission.admin()")
    @PutMapping("/{loopId}")
    public ResponseResult updateLoop(@PathVariable("loopId") String loopId, @RequestBody Looper looper) {
        return loopService.updateLoop(loopId,looper);
    }

    @PreAuthorize("permission.admin()")
    @GetMapping("/{loopId}")
    public ResponseResult getLoop(@PathVariable("loopId") String loopId) {
        return loopService.getLoop(loopId);
    }

    @GetMapping("/list")
    public ResponseResult listLoop() {
        return loopService.listLoop();
    }
}
package com.it.blog.controller.admin;

import com.it.blog.response.ResponseResult;
import com.it.blog.services.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/admin/image")
public class ImageAdminApi {

    @Autowired
    private IImageService imageService;

    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult uploadImage(@RequestParam("file") MultipartFile file) {

        return imageService.uploadImage(file);
    }

    @PreAuthorize("@permission.admin()")
    @PostMapping("/{imageId}")
    public ResponseResult deleteImage(@PathVariable("imageId") String imageId) {
        return imageService.deleteById(imageId);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/{imageId}")
    public void getImage(HttpServletResponse response ,@PathVariable("imageId") String imageId) {
        try {
            imageService.viewImage(response, imageId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listImages(@RequestParam("page") int page,@RequestParam("size") int size) {
        return imageService.listImages(page, size);
    }
}

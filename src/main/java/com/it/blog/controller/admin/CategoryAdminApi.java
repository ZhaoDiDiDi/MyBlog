package com.it.blog.controller.admin;

import com.it.blog.pojo.Category;
import com.it.blog.response.ResponseResult;
import com.it.blog.services.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理中心，分类的API
 */
@RestController
@RequestMapping("/admin/category")
public class CategoryAdminApi {

    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加分类
     * 需要管理员权限
     * @param category
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping("/add")
    public ResponseResult addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    /**
     * 删除分类
     * @param categoryId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    /**
     * 更新分类
     * @param categoryId
     * @param category
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{categoryId}")
    public ResponseResult updateCategory(@PathVariable("categoryId") String categoryId, @RequestBody Category category) {
        return categoryService.updateCategory(categoryId, category);
    }

    /**
     * 获取分类
     * <p>
     * 使用case：修改的时候过去内容填充弹窗
     * 不获取也是可以的，从列表里获取数据
     * <p>
     *  权限：管理员权限
     * @param categoryId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.getCategory(categoryId);
    }

    /**
     * 获取分类列表
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listCategory() {
        return categoryService.listCategories();
    }
}

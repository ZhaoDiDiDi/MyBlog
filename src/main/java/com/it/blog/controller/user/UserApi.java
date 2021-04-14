package com.it.blog.controller.user;

import com.it.blog.pojo.SobUser;
import com.it.blog.response.ResponseResult;
import com.it.blog.services.IUserService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    @Autowired
    private IUserService userService;

    /**
     * 初始化管理账号 init—admin
     * @param sobUser
     * @return
     */
    @PostMapping("/admin_account")
    public ResponseResult initMangerAccount(@RequestBody SobUser sobUser, HttpServletRequest request) {
        log.info("user name ==> " + sobUser.getUserName());
        log.info("password ==> " + sobUser.getPassword());
        log.info("email ==> " + sobUser.getEmail());
        return userService.initManagerAccount(sobUser, request);
    }

    /**
     * 注册join-in
     * @param sobUser
     * @return
     */
    @PostMapping("/join_in")
    public ResponseResult register(@RequestBody SobUser sobUser,
                                   @RequestParam("email_code") String emailCode,
                                   @RequestParam("captcha_code") String captchaCode,
                                   @RequestParam("captcha_key") String captchaKey,
                                   HttpServletRequest request) {
        return userService.register(sobUser, emailCode, captchaCode, captchaKey, request);
    }

    /**
     * 登录
     * <p>
     *需要提交的数据
     *  1、用户账号-可以昵称，可以邮箱--->做了唯一处理
     *  2、密码
     *  3、图灵验证码
     *  4、图灵验证的key
     * @param captcha   图灵验证码
     * @param sobUser   用户bean类。封装着账号和密码
     * @param captchakey 图灵验证码的key
     * @return
     */
    @PostMapping("/login/{captcha}/{captcha_key}")
    public ResponseResult login(@PathVariable("captcha") String captcha,
                                @PathVariable("captcha_key") String captchakey,
                                @RequestBody SobUser sobUser
                                ) {
        return userService.doLogin(captcha, captchakey, sobUser);
    }



    /**
     * 获取图灵验证码
     * 有效时长10分钟
     * @param response
     * @param captchaKey
     */
    @GetMapping("/captcha")
    public void getCahcha(HttpServletResponse response, @RequestParam("captcha_key") String captchaKey) throws Exception {

        try {
            userService.createCaptcha(response, captchaKey);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 发送邮箱验证码
     * <p>
     *     使用场景：注册、找回密码、修改密码（会输入新的邮箱）
     *      注册：如果已经注册过了，就会提示，该邮箱已注册
     *      找回密码：如果没有注册过，提示该邮箱没有注册
     *      修改邮箱（新的邮箱）：如果已经注册了，提示该邮箱已经注册
     *
     *
     *      注册(register)：如果已经注册过了，就会提示，该邮箱已注册
     *      找回密码(forget)：如果没有注册过，提示该邮箱没有注册
     *      修改邮箱(update)（新的邮箱）：如果已经注册了，提示该邮箱已经注册
     * @param request
     * @param emailAddress
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(HttpServletRequest request,
                                         @RequestParam("email") String emailAddress,
                                         @RequestParam("type") String type) {
        log.info("email == >" + emailAddress);
        return userService.sendEmail(type, request, emailAddress);
    }

    /**
     * 修改密码password
     * 普通做法：通过旧密码对比来新密码
     * <p>
     * 即可以找回密码，也可以修改密码
     * 发送验证码到邮箱/手机-->判断验证码是否为真来判断
     * 对应邮箱/手机号码注册的账号是否属于你
     * <p>
     * 步骤：
     * 1、用户填写邮箱
     * 2、用户获取验证码 type=forget
     * 3、填写验证码
     * 4、填写新的密码
     * 5、提交数据
     * <p>
     * 数据包括：
     * <p>
     * 1、邮箱和新密码
     * 2、验证码
     * <p>
     * 如果验证码正确-->所用邮箱注册就是你的，可以修改密码
     * @param sobUser
     * @return
     */
    @PutMapping("/password/{verifyCode}")
    public ResponseResult updatePassword(@PathVariable("verifyCode") String verifyCode,
                                         @RequestBody SobUser sobUser) {
        return userService.updateUserPassword(verifyCode, sobUser);
    }

    /**
     * 获取作者信息user-info
     * @param userId
     * @return
     */
    @GetMapping("/user_info/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 修改用户信息user-info
     * <p>
     * 允许用户修改的内容
     * 1、头像
     * 2、用户名（唯一的）
     * 2.5、签名
     * 3、密码（单独修改）
     * 4、Email（唯一的，单独修改）
     * @param userId
     * @param sobUser
     * @return
     */
    @PutMapping("/user_info/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId,
                                         @RequestBody SobUser sobUser) {
        return userService.updateUserInfo(userId, sobUser);
    }

    /**
     * 获取用户列表
     * 权限：管理员权限
     *
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listUser(@RequestParam("page") int page,
                                   @RequestParam("size") int size) {
        return userService.listUsers(page, size);
    }

    /**
     * 需要管理员权限
     * @param userId
     * @return
     */
    @DeleteMapping("/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId") String userId) {
        //判断当前操作的用户是谁
        //根据用户角色判断是否可以删除
        //TODO:通过注解的方式设置权限
        return userService.deleteUserById(userId);
    }

    /**
     * 检测Email是否已经注册
     *
     * @param email 邮箱地址
     * @return  SUECCESS --> 已经注册了 FARILED --> 没有注册
     */
    @ApiResponses({
            @ApiResponse(code = 20000, message = "表示当前邮箱已经注册了"),
            @ApiResponse(code = 40000, message = "表示当前邮箱未注册")
    })
    @GetMapping("/email")
    public ResponseResult checkEmail(@RequestParam("email") String email) {
        return userService.checkEmail(email);
    }

    /**
     * 检测userName是否已经注册
     *
     * @param userName 用户名
     * @return  SUECCESS --> 已经注册了 FARILED --> 没有注册
     */
    @ApiResponses({
            @ApiResponse(code = 20000, message = "表示当用户名已经注册了"),
            @ApiResponse(code = 40000, message = "表示当用户名未注册")
    })
    @GetMapping("/user_name")
    public ResponseResult checkUserName(@RequestParam("userName") String userName) {
        return userService.checkUserName(userName);
    }

    /**
     * 1、必须已经登录了
     * 2、新的邮箱没有注册过
     * <p>
     * 用户的步骤：
     * 1、已经登录
     * 2、输入新的邮箱地址
     * 3、获取验证码
     * 4、输入验证码
     * 5、提交数据
     * <p>
     * 需要提交的数据
     * 1、新的邮箱地址
     * 2、验证码
     * 3、其他信息我们可以token获取
     * @return
     */
    @PutMapping("/email")
    public ResponseResult updateEmail(@RequestParam("email") String email,
                                      @RequestParam("verify_code") String verifyCode) {
        return userService.updateEmail(email, verifyCode);
    }

    /**
     * 退出登录
     * <p>
     * 拿到token_key
     * -> 删除redis里对应的token
     * -> 删除mysql里对应的refreshToken
     * -> 删除cookie里的token_key
     * @return
     */
    @GetMapping("/logout")
    public ResponseResult logout() {
        return userService.doLoginOut();
    }
}

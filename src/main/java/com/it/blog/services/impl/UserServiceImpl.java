package com.it.blog.services.impl;

import com.google.gson.Gson;
import com.it.blog.dao.RefreshTokenDao;
import com.it.blog.dao.SettingsDao;
import com.it.blog.dao.UserDao;
import com.it.blog.pojo.RefreshToken;
import com.it.blog.pojo.Setting;
import com.it.blog.pojo.SobUser;
import com.it.blog.response.ResponseResult;
import com.it.blog.response.ResponseState;
import com.it.blog.services.IUserService;
import com.it.blog.utils.*;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * 初始化管理员账号
 */

@Slf4j
@Service
@Transactional //事务管理
public class UserServiceImpl extends BaseService implements IUserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private IdWorker idWorker; //注入生成ID （雪花算法）

    @Autowired
    private UserDao userDao;

    @Autowired
    private SettingsDao settingsDao;

    @Autowired
    private RefreshTokenDao refreshTokenDao;

    @Autowired
    private Gson gson;

    private HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }
    private HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getResponse();
    }

    @Override
    public ResponseResult initManagerAccount(SobUser sobUser, HttpServletRequest request) {
        //检查是否有初始化
        Setting managerAccountState = settingsDao.findOneByKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        if (managerAccountState != null) {
            return ResponseResult.FAILED("管理员账号已经初始化了");
        }
        //检查数据
        if (TextUtils.isEmpty(sobUser.getUserName())) {
            return ResponseResult.FAILED("用户名不能为空");
        }
        if (TextUtils.isEmpty(sobUser.getPassword())) {
            return ResponseResult.FAILED("密码不能为空");
        }
        if (TextUtils.isEmpty(sobUser.getEmail())) {
            return ResponseResult.FAILED("邮箱不能为空");
        }
        //补充数据
        sobUser.setId(String.valueOf(idWorker.nextId()));
        sobUser.setRoles(Constants.User.ROLE_ADMIN);
        sobUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        sobUser.setState(Constants.User.DEFAULT_STATE);
        String remoteAddr = request.getRemoteAddr();
        String localAddr = request.getLocalAddr();
        log.info("remoteAddr ==> " + remoteAddr);
        log.info("localAddr ==> " + localAddr);
        sobUser.setLoginIp(remoteAddr);
        sobUser.setRegIp(localAddr);
        sobUser.setCreateTime(new Date());
        sobUser.setUpdateTime(new Date());
        //对密码进行加密
        //原密码
        String password = sobUser.getPassword();
        String encode = bCryptPasswordEncoder.encode(password);
        sobUser.setPassword(encode);
        //保存到数据库里
        userDao.save(sobUser);


        //更新已添加的标记
        //肯定错的
        Setting setting = new Setting();
        setting.setId(idWorker.nextId() + "");
        setting.setKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        setting.setCreateTime(new Date());
        setting.setUpdateTime(new Date());
        setting.setValue("1");
        settingsDao.save(setting);
        return ResponseResult.SUCCESS("初始化成功");
    }

    public static final int[] captcha_font_types = {
            Captcha.FONT_1,
            Captcha.FONT_2,
            Captcha.FONT_3,
            Captcha.FONT_4,
            Captcha.FONT_5,
            Captcha.FONT_6,
            Captcha.FONT_7,
            Captcha.FONT_8,
            Captcha.FONT_9,
            Captcha.FONT_10
    };

    @Autowired
    private Random random;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void createCaptcha(HttpServletResponse response, String captchaKey) throws Exception {
        if (TextUtils.isEmpty(captchaKey) || captchaKey.length() < 13) {
            return;
        }
        long key;
        try {
            key = Long.parseLong(captchaKey);
        } catch (NumberFormatException e) {
            return;
        }

        //可以用了
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        int captchaType = random.nextInt(3);
        Captcha targetCaptcha = null;
        if (captchaType == 0) {
            // 三个参数分别为宽、高、位数
            SpecCaptcha specCaptcha = new SpecCaptcha(200, 60, 5);
        } else if (captchaType == 1) {
            //gif类型
            targetCaptcha = new GifCaptcha(200, 60);
        } else {
            //算术类型
            targetCaptcha = new ArithmeticCaptcha(200, 60);
            targetCaptcha.setLen(2);  // 几位数运算，默认是两位
            targetCaptcha.text();  // 获取运算的结果
        }
        //设置字体
        int index = random.nextInt(captcha_font_types.length);
        log.info("captcha font type index ==> " + index);
        //设置字体效果
        targetCaptcha.setFont(captcha_font_types[index]);
        //设置验证码字符类型
        targetCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        //转换大小写
        String content = targetCaptcha.text().toLowerCase();
        log.info("captcha content == > " + content);
        //保存到redis ,十分钟有效
        redisUtils.set(Constants.User.KEY_CAPTCHA_CONTENT + key, content, 60 * 10);
        // 输出图片流
        targetCaptcha.out(response.getOutputStream());
    }

    @Autowired
    private TaskService taskService;

    /**
     * 发送邮箱验证码
     * 使用场景：注册、找回密码、修改密码（会输入新的邮箱）
     *      注册(register)：如果已经注册过了，就会提示，该邮箱已注册
     *      找回密码(forget)：如果没有注册过，提示该邮箱没有注册
     *      修改邮箱(update)（新的邮箱）：如果已经注册了，提示该邮箱已经注册
     * @param request
     * @param emailAddress
     * @return
     */
    //先检验后记录
    @Override
    public ResponseResult sendEmail(String type, HttpServletRequest request, String emailAddress) {
        if (emailAddress == null) {
            return ResponseResult.FAILED("邮箱地址不可以为空");
        }
        //根据类型。查询邮箱是否存在
        if ("register".equals(type) || "update".equals(type)) {
            SobUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail != null) {
                return ResponseResult.FAILED("该邮箱已注册");
            }
        } else if ("forget".equals(type)) {
            SobUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail == null) {
                return ResponseResult.FAILED("该邮箱并未注册");
            }
        }
        //1. 防止暴力发送（不断发送）：同一个邮箱要间隔30秒发一次，同一个Ip，一小时内，最多只能发10次（如果是短信最多只能发5次）
        String remoteAddr = request.getRemoteAddr();//远程地址
        log.info("sendEmail ==> ip ==>" + remoteAddr);
        if (remoteAddr != null) {
            remoteAddr = remoteAddr.replaceAll(":", "_");
        }
        //拿ipSendTime出来，如果为null则过了
        log.info("Constans.User.KEY_EMAIL_SEND_IP + remoteAddr ==> " + Constants.User.KEY_EMAIL_SEND_IP + remoteAddr);
        Integer ipSendTime = (Integer) redisUtils.get(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr);    //从redis取出次数
        if (ipSendTime != null && ipSendTime > 10) {
            return ResponseResult.FAILED("您发送验证码也太频繁了把！");
        }
        Object hasEmailSend = redisUtils.get(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress);
        if (hasEmailSend != null) {
            return ResponseResult.FAILED("您发送验证码也太频繁了把！");
        }
        //2. 检查邮件地址是否正确
        boolean isEmailFormatOk = TextUtils.isEmailAddressOk(emailAddress);
        if (!isEmailFormatOk) {
            return ResponseResult.FAILED("邮箱地址格式不正确");
        }
        //3. 发送验证码6位数 100000~999999
        //0~999999
        int code = random.nextInt(999999);
        if (code < 100000) {
            code += 100000;
        }
        log.info("邮箱验证码 ==> " + code);
        try {

            //通过异步解决发送慢的问题
            taskService.sendEmailVerifyCode(String.valueOf(code),emailAddress);
        } catch (Exception e) {
            return ResponseResult.FAILED("验证码发送失败，请稍后重试");
        }
        //4. 做记录
        //发送记录code 若验证码发送次数为null（刚发送）则开始记间
        if (ipSendTime == null) {
            ipSendTime = 0;
        }
        ipSendTime++;
        //1小时有效
        redisUtils.set(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr, ipSendTime, 60 * 60); //从redis写入次数
        redisUtils.set(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress, "true", 30);
        //保存code,十分钟内有效
        redisUtils.set(Constants.User.KEY_EMAIL_CODE_CONTENT + emailAddress, String.valueOf(code), 60 * 10);
        return ResponseResult.SUCCESS("验证码发送成功");
    }

    @Override
    public ResponseResult register(SobUser sobUser, String emailCode, String captchaCode, String captchaKey, HttpServletRequest request) {

        //第一步:检查当前用户名是否已经注册
        String userName = sobUser.getUserName();
        if (TextUtils.isEmpty(userName)) {
            return ResponseResult.FAILED("用户名不可以为空");
        }
        SobUser userByName = userDao.findOneByUserName(userName);
        if (userByName != null) {
            return ResponseResult.FAILED("该用户已经注册");
        }
        //第二步:检查邮箱格式是否正确
        String email = sobUser.getEmail();
        if (TextUtils.isEmpty(email)) {
            return ResponseResult.FAILED("邮箱地址不可以为空");
        }
        if (!TextUtils.isEmailAddressOk(email)) {
            return ResponseResult.FAILED("邮箱地址格式不正确");
        }
        //第三步:检查该邮箱是否已经注册
        SobUser userByEmail = userDao.findOneByEmail(email);
        if (userByEmail != null) {
            return ResponseResult.FAILED("该邮箱地址已被注册");
        }
        //第四步:检查邮箱验证码是否正确
        String emailVerifCode = (String) redisUtils.get(Constants.User.KEY_EMAIL_CODE_CONTENT + sobUser.getEmail());
        log.info("emailVerifCode == >" + emailVerifCode);
        if (TextUtils.isEmpty(emailVerifCode)) {
            return ResponseResult.FAILED("邮箱验证码已过期");
        }
        if (!emailVerifCode.equals(emailCode)) {
            return ResponseResult.FAILED("邮箱验证码不正确");
        } else {
            //正确 干掉验证码
            redisUtils.del(Constants.User.KEY_CAPTCHA_CONTENT + email);
        }
        //第五步:检查图灵验证码是否正确
        String captchaVerifyCode = (String) redisUtils.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        if (TextUtils.isEmpty(captchaVerifyCode)) {
            return ResponseResult.FAILED("人类验证码无效");
        }
        if (!captchaVerifyCode.equals(captchaCode)) {
            return ResponseResult.FAILED("人类验证码不正确");
        } else {
            redisUtils.del(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        }
        //达到可以注册的条件
        //第六步:对密码进行加密
        String password = sobUser.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不可以为空");
        }
        sobUser.setPassword(bCryptPasswordEncoder.encode(sobUser.getPassword()));
        //第七步:补全数据
        //包括:注册IP,登录IP,角色,头像,创建时间,更新时间
        String ipAddress = request.getRemoteAddr();
        sobUser.setRegIp(ipAddress);
        sobUser.setLoginIp(ipAddress);
        sobUser.setUpdateTime(new Date());
        sobUser.setCreateTime(new Date());
        sobUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        sobUser.setRoles(Constants.User.ROLE_NORMAl);
        sobUser.setId(idWorker.nextId() + "");
        sobUser.setState("1");
        //第八步:保存到数据库中
        userDao.save(sobUser);
        //第九步:返回结果
        return ResponseResult.GET(ResponseState.JOIN_IN_SUCCESS);
    }

    @Override
    public ResponseResult doLogin(String captcha,
                                  String captchakey,
                                  SobUser sobUser) {
        String captchaValue = (String) redisUtils.get(Constants.User.KEY_CAPTCHA_CONTENT + captchakey);
        if (!captcha.equals(captchaValue)) {
            return ResponseResult.FAILED("人类验证码不正确");
        }
        //验证成功删除redis里的验证码
        redisUtils.del(Constants.User.KEY_CAPTCHA_CONTENT + captchakey);
        //有可能是邮箱，也可能是用户名
        String userName = sobUser.getUserName();
        if (TextUtils.isEmpty(userName)) {
            return ResponseResult.FAILED("账号不可以为空");
        }
        String password = sobUser.getPassword();
        log.info("password ===>" + password);
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不可以为空");
        }

        SobUser userFromDb = userDao.findOneByUserName(userName);
        //若用户名为空则尝试邮箱
        if (userFromDb == null) {
            userFromDb = userDao.findOneByEmail(userName);
        }
        if (userFromDb == null) {
            return ResponseResult.FAILED("用户名或密码错误");
        }

        //用户存在
        //对比密码
        boolean matches = bCryptPasswordEncoder.matches(password, userFromDb.getPassword());
        if (!matches) {
            return ResponseResult.FAILED("用户名或密码错误");
        }
        //判断账号状态
        if (!"1".equals(userFromDb.getState())) {
            return ResponseResult.ACCOUNT_DENIED();
        }
        //密码正确,创建token
        createToken(getResponse(), userFromDb);
        return ResponseResult.SUCCESS("登录城功");
    }

    /**
     *
     * @param response
     * @param userFromDb
     * @return token_key
     */
    private String createToken(HttpServletResponse response, SobUser userFromDb) {
        int deleteResult = refreshTokenDao.deleteAllByUserId(userFromDb.getId());
        log.info("deleteResult ==> " + deleteResult);
        //生成token
        //token默认有效为2个小时
        Map<String, Object> claims = ClaimsUtils.sobUser2Claims(userFromDb);
        String token = JwtUtil.createToken(claims);
        //返回token的md5值，token会保存到redis里
        //前端访问的时候，携带token的md5key，从redis中获取即可
        String tokenKey = DigestUtils.md5DigestAsHex(token.getBytes());
        //保存到token到redis里，有效期为2个小时，key是tokenkey
        redisUtils.set(Constants.User.KEY_TOKEN + tokenKey, token, Constants.TimeValueInMillions.HOUR_2);
        //把token写到cookie里
        //从request中动态获取
        CookieUtils.setUpCookie(response, Constants.User.COOKIC_TOKEN_KEY, tokenKey);
        //生成refreshToken
        String refreshTokenValue = JwtUtil.createRefreshToken(userFromDb.getId(), Constants.TimeValueInMillions.MONTH);
        //保存到数据库里
        //refreshtoken, tokenkey, 用户ID， 创建时间， 更新时间
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(idWorker.nextId() + "");
        refreshToken.setRefreshToken(refreshTokenValue);
        refreshToken.setUserId(userFromDb.getId());
        refreshToken.setTokenKey(tokenKey);
        refreshToken.setCreateTime(new Date());
        refreshToken.setUpdateTime(new Date());
        refreshTokenDao.save(refreshToken);

        return tokenKey;
    }

    /**
     * 本质，检查用户是否已经登录
     * @return
     */
    @Override
    public SobUser checkSobUser() {
        //拿到token
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKIC_TOKEN_KEY);
        SobUser sobUser = parseByTokenKey(tokenKey);
        if (sobUser == null) {
            //说明解析出错了，过期了
            //1、去mysql查询refreshtoken
            RefreshToken refreshToken = refreshTokenDao.findOneByTokenKey(tokenKey);
            //2、如果不存在，就是当前访问没有登录，提示用户登录
            if (refreshToken == null) {
                log.info("refreshToken is null ...");
                return null;
            }
            //3、如果存在，就解析refreshtoken
            try {
                JwtUtil.parseJWT(refreshToken.getRefreshToken());//如果出错就过期了 直接跳到catch
                //5、如果refreshToken有效，创建新的token和新的refreshToken
                String userId = refreshToken.getUserId();
                SobUser userFromDb = userDao.findOneById(userId);
                //删除掉refreshToken的记录
                String newTokenKey = createToken(getResponse(), userFromDb);
                //返回token
                return parseByTokenKey(newTokenKey);
            } catch (Exception exception) {
                //4、如果refreshToken过期了，就当前访问没有登录，提示用户登录
                return null;
            }
        }
        return sobUser;
    }

    @Override
    public ResponseResult getUserInfo(String userId) {
        //从数据库里获取
        SobUser user = userDao.findOneById(userId);
        //判断结果
        if (user == null) {
            //如果不存在，就返回不存在
            return ResponseResult.FAILED("用户不存在");
        }
        //如果存在，就复制对象，清空密码、Email、登录IP、注册IP
        String userJson = gson.toJson(user);
        SobUser newSobuUer = gson.fromJson(userJson, SobUser.class);
        newSobuUer.setPassword("");
        newSobuUer.setEmail("");
        newSobuUer.setRegIp("");
        newSobuUer.setLoginIp("");
        //返回结果
        return ResponseResult.SUCCESS("获取成功").setData(newSobuUer);
    }

    @Override
    public ResponseResult checkEmail(String email) {
        SobUser user = userDao.findOneByEmail(email);
        return user == null ? ResponseResult.FAILED("邮箱未注册") : ResponseResult.SUCCESS("该邮箱已注册");
    }

    @Override
    public ResponseResult checkUserName(String userName) {
        SobUser user = userDao.findOneByUserName(userName);
        return user == null ? ResponseResult.FAILED("该用户未注册") : ResponseResult.SUCCESS("该用户已注册");
    }

    /**
     * 更新用户信息
     * @param userId
     * @param sobUser
     * @return
     */
    @Override
    public ResponseResult updateUserInfo(String userId,
                                         SobUser sobUser) {
        //从token里解析出来的user，为了校验权限
        //只有用户才可以修改自己的信息
        SobUser userFromKey = checkSobUser();
        if (userFromKey == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        SobUser userFromDb = userDao.findOneById(userFromKey.getId());

        //判断用户的id和即将要修改的ID是否一致，如果一致才可以修改
        if (!userFromDb.getId().equals(userId)) {
            return ResponseResult.PERMISSION_DENIED();
        }
        //可以进行修改
        //可以进行修改的项
        //用户名
        String userName = sobUser.getUserName();
        if (!TextUtils.isEmpty(userName)) {
            SobUser userByUserName = userDao.findOneByUserName(userName);
            if (userByUserName != null) {
                return ResponseResult.FAILED("该用户已经注册");
            }
            userFromDb.setUserName(userName);
        }
        //头像
        if (!TextUtils.isEmpty(sobUser.getAvatar())) {
            userFromDb.setAvatar(sobUser.getAvatar());
        }
        //签名，可以为空
        userFromDb.setSign(sobUser.getSign());
        userDao.save(userFromDb);
        //干掉redis里的token，下一次请求，需要解析token的，就会根据refreshToken从新创建一个
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKIC_TOKEN_KEY);
        redisUtils.del(tokenKey);
        return ResponseResult.SUCCESS("用户信息更新成功");
    }

    /**
     * 删除用户： 并不是真的删除
     * 而是修改状态
     * <p>
     * ps:需要管理员权限
     * @param userId
     * @return
     */
    @Override
    public ResponseResult deleteUserById(String userId) {
        //可以删除用户了
        int result = userDao.deleteUserByState(userId);
        if (result > 0) {
            return ResponseResult.SUCCESS("删除成功");
        }
        return ResponseResult.FAILED("用户不存在");
    }

    /**
     *获取用户列表
     * 权限：管理员权限
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult listUsers(int page, int size) {
        //可以获取用户列表
        //分页查询
        //参数检测
        page = chcekPage(page);
        size = chcekPage(size);

        //根据注册日期来排序
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<SobUser> all = userDao.findAll(pageable);
        return ResponseResult.SUCCESS("获取用户列表成功").setData(all);
    }

    /**
     * 更新密码
     * @param verifyCode
     * @param sobUser
     * @return
     */
    @Override
    public ResponseResult updateUserPassword(String verifyCode, SobUser sobUser) {
        //检测邮箱是否有填写
        String email = sobUser.getEmail();
        if (TextUtils.isEmpty(email)) {
            return ResponseResult.FAILED("邮箱不可以为空");
        }
        //根据邮箱去redis里拿验证
        //进行对比
        String redisVerifyCode = (String) redisUtils.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (redisVerifyCode == null || !redisVerifyCode.equals(verifyCode)) {
            return ResponseResult.FAILED("验证码错误");
        }
        redisUtils.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        int result = userDao.updatePasswordByEmail(bCryptPasswordEncoder.encode(sobUser.getPassword()), email);
        //修改密码
        return result > 0 ? ResponseResult.SUCCESS("修改密码成功") : ResponseResult.FAILED("修改密码失败");
    }

    /**
     * 修改邮箱
     * @param email
     * @param verifyCode
     * @return
     */
    @Override
    public ResponseResult updateEmail(String email, String verifyCode) {
        //1、确保用户已经登录了
        SobUser sobUser = this.checkSobUser();
        //没有登录
        if (sobUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //2、对比验证码，确保新的邮箱地址是属于当前用户的
        String redisVerifyCode = (String) redisUtils.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (TextUtils.isEmpty(redisVerifyCode) || !redisVerifyCode.equals(verifyCode)) {
            return ResponseResult.FAILED("验证码错误");
        }
        //验证正确，删除验证码
        redisUtils.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        //可以修改邮箱
        int result = userDao.updateEmialById(email, sobUser.getId());
        return result > 0 ? ResponseResult.SUCCESS("修改邮箱成功") : ResponseResult.FAILED("修改邮箱失败");
    }

    @Override
    public ResponseResult doLoginOut() {
        //拿到token_key
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKIC_TOKEN_KEY);
        if (TextUtils.isEmpty(tokenKey)) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //删除redis里的token
        redisUtils.del(Constants.User.KEY_TOKEN + tokenKey);
        //删除mysql里的refreshToken
        refreshTokenDao.deleteAllByTokenKey(tokenKey);
        //删除cookie里的token_key
        CookieUtils.deleteCookie(getResponse(), Constants.User.COOKIC_TOKEN_KEY);
        return ResponseResult.SUCCESS("退出登录成功");
    }

    private SobUser parseByTokenKey(String tokenKey) {
        String token = (String) redisUtils.get(Constants.User.KEY_TOKEN + tokenKey);
        if (token != null) {
            try {
                Claims claims = JwtUtil.parseJWT(token);
                return ClaimsUtils.claims2SobUser(claims);
            } catch (Exception e) {
                log.info("parseByTokenKey ==> " + tokenKey + "过期了...");
                return null;
            }
        }
        return null;
    }
}

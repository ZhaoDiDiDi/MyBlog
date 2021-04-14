package com.it.blog.utils;

public interface Constants {

    int DEFAULT_SIZE = 30;

    interface User{
        String ROLE_ADMIN = "role_admin";
        String ROLE_NORMAl = "role_normal";
        String DEFAULT_AVATAR = "https://cdn.sunofbeaches.com/images/default_avatar.png";
        String DEFAULT_STATE = "1";
        String COOKIC_TOKEN_KEY = "sob_blog_token";
        //redis的key
        String KEY_CAPTCHA_CONTENT = "key_captcha_content_";
        String KEY_EMAIL_CODE_CONTENT = "key_email_code_content_";
        String KEY_EMAIL_SEND_IP = "key_email_send_ip_";
        String KEY_EMAIL_SEND_ADDRESS = "key_email_send_address_";
        String KEY_TOKEN = "key_token_";
    }

    interface Settings {
        String MANAGER_ACCOUNT_INIT_STATE = "manager_account_init_state";
        String WEB_SIZE_TITLE = "web_size_title";
        String WEB_SIZE_DESCRIPTION = "web_size_description";
        String WEB_SIZE_KEYWORDS = "web_size_keywords";
        String WEB_SIZE_COUNT = "web_size_count";
    }

    interface ImageType {
        String PREFIX = "image/";
        String TYPE_JPG = "jpg";
        String TYPE_PNG = "png";
        String TYPE_GIF = "gif";
        String TYPE_JPEG = "jpeg";
        String TYPE_JPG_WITH_PREFIX = PREFIX + "jpg";
        String TYPE_PNG_WITH_PREFIX = PREFIX + "png";
        String TYPE_GIF_WITH_PREFIX = PREFIX + "gif";
        String TYPE_JPEG_WITH_PREFIX = PREFIX + "jpeg";
    }

    interface Page{
        int DEFUALT_PAGE = 1;
        int MIN_SIZE = 2;

    }

    /**
     * 单位是毫秒
     */
    interface TimeValueInMillions {
        long MIN = 60 * 1000;
        long HOUR = 60 * MIN;
        long HOUR_2 = 60 * MIN * 2;
        long DAY = HOUR * 24;
        long WEEK = DAY * 7;
        long MONTH = DAY * 30;
    }

    /**
     * 单位是秒
     */
    interface TimeValueInSecond {
        int MIN = 60;
        int HOUR = 60 * MIN;
        int HOUR_2 = 60 * MIN * 2;
        int DAY = HOUR * 24;
        int WEEK = DAY * 7;
        int MONTH = DAY * 30;
    }

    interface Article {
        int TITLE_MAX_LENGTH = 128;
        int SUMMARY_MAX_LENGTH = 256;
        //0表示删除、1表示已经发布、2表示草稿、3、表示置顶
        String STATE_DELETE = "0";
        String STATE_PUBLISH = "1";
        String STATE_DRAFT = "2";
        String STATE_TOP = "3";
    }
}

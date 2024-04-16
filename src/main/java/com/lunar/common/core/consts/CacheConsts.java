package com.lunar.common.core.consts;

/**
 * 缓存常量
 */
public class CacheConsts {

    /**
     * 缓存刷新时间（s），默认24小时
     */
    public final static long REFRESH_TIME = 24 * 60 * 60;

    /**
     * 缓存有效期（s），默认3天=72小时
     */
    public final static long EXPIRATION = 3 * 24 * 60 * 60;

    /**
     * redis cache默认过期时间 30天
     */
    public final static long CACHE_TIMEOUT_DAY = 30;

    /**
     * redis锁前缀
     */
    public final static String REDIS_LOCK_PRE = Consts.APP_NAME + ":lock:";

    /**
     * token缓存前缀 dct:tokens:{companyId}:{userId}
     */
    public final static String LOGIN_TOKEN_KEY = Consts.APP_NAME + ":tokens:";

    /**
     * 验证码ipkey "dct:vfcode:ip:{ipaddr}
     */
    public final static String REDIS_VFCODE_IP = Consts.APP_NAME + ":vfcode:ip:%s";

    /**
     * 异地token前缀
     */
    public final static String TOKEN_OTHER_LOGIN_KEY = Consts.APP_NAME + ":other_tokens";

    /**
     * 用户语言缓存
     */
    public final static String I18N_USER_KEY = Consts.APP_NAME + ":i18n:user";

    /**
     * 多语言缓存前缀
     */
    public final static String I18N_LANG_KEY = Consts.APP_NAME + ":i18n:lang:";

    /**
     * 验证码key dct:vfcode:{userid}
     */
    public final static String REDIS_VFCODE = Consts.APP_NAME + ":vfcode:%s";

    /**
     * 权限点名称缓存
     */
    public final static String PERM_NAME_KEY = Consts.APP_NAME + ":perms:name";

    /**
     * 登录错误次数缓存 dct:login.error:{companyCode}:{username}
     */
    public static final String LOGIN_ERROR_KEY = Consts.APP_NAME + ":login.error:%s:%s";

    /**
     * 登陆连续错误锁定时长 30分钟
     */
    public final static long LOGIN_ERROR_TIMEOUT = 30L;

    /**
     * 登陆连续错误次数
     */
    public final static int LOGIN_ERROR_TIMES = 5;

    /**
     * 图形验证码 dct:captcha_codes:{uuid}
     */
    public static final String CAPTCHA_KEY = Consts.APP_NAME + ":captcha_codes:%s";

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 2L;

}

package com.lunar.common.redis.utils;

import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.consts.SwitchConsts;
import com.lunar.common.core.utils.StringUtils;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.consts.SwitchConsts;
import com.lunar.common.core.utils.StringUtils;

/**
 * @author szx
 * @date 2022/09/16 14:26
 */
public class I18nUtil {

    /**
     * 多语言开关
     */
    private static final boolean LANG_SWITCH = SwitchConsts.LANG_SWITCH;

    /**
     * 查询用户语言，查询不到默认使用中文
     *
     * @param userId
     * @return
     */
    public static String getUserLang(Long userId) {
        return getUserLang(String.valueOf(userId));
    }

    /**
     * 查询用户语言，查询不到默认使用中文
     *
     * @param userId
     * @return
     */
    public static String getUserLang(String userId) {
        if (!LANG_SWITCH) {
            return "";
        }

        String lang = RedisUtil.hget(CacheConsts.I18N_USER_KEY, userId);
        // 默认中文
        return StringUtils.isBlank(lang) ? Consts.ZH_KEY : lang;
    }

    /**
     * 缓存用户语言
     *
     * @param lang
     * @param userId
     */
    public static void setUserLang(String lang, Long userId) {
        if (!LANG_SWITCH) {
            return;
        }

        RedisUtil.hset(CacheConsts.I18N_USER_KEY, String.valueOf(userId), lang);
    }

    /**
     * 语言缓存redisKey
     *
     * @param lang
     * @return
     */
    public static String getRedisKey(String lang) {
        return CacheConsts.I18N_LANG_KEY + lang;
    }

    /**
     * 根据field查询缓存对应翻译，查询为空则使用默认msg
     *
     * @param lang
     * @param field
     * @param defaultStr
     * @return
     */
    public static String getMsgDefaultIfBlank(String lang, String field, String defaultStr) {
        if (!LANG_SWITCH) {
            return defaultStr;
        }

        String redisKey = I18nUtil.getRedisKey(lang);
        String cacheMsg = RedisUtil.hget(redisKey, field);
        return StringUtils.defaultIfBlank(cacheMsg, defaultStr);
    }

}

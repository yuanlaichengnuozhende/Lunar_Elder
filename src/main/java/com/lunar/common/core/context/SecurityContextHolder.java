package com.lunar.common.core.context;

import cn.hutool.core.convert.Convert;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.lunar.common.core.consts.SecurityConsts;
import com.lunar.common.core.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取当前线程变量中的 用户id、用户名称、Token等信息 注意： 必须在网关通过请求头的方法传入，同时在HeaderInterceptor拦截器设置值。 否则这里无法获取
 */
public class SecurityContextHolder {

    private static final TransmittableThreadLocal<Map<String, Object>> THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> map = getLocalMap();
        map.put(key, value == null ? StringUtils.EMPTY : value);
    }

    public static String get(String key) {
        Map<String, Object> map = getLocalMap();
        return Convert.toStr(map.getOrDefault(key, StringUtils.EMPTY));
    }

    public static <T> T get(String key, Class<T> clazz) {
        Map<String, Object> map = getLocalMap();
        return (T) map.getOrDefault(key, null);
    }

    public static Map<String, Object> getLocalMap() {
        Map<String, Object> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new ConcurrentHashMap<String, Object>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, Object> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }

    public static Long getUserId() {
        return Convert.toLong(get(SecurityConsts.USER_ID), 0L);
    }

    public static void setUserId(String account) {
        set(SecurityConsts.USER_ID, account);
    }

    public static String getUserLang() {
        return get(SecurityConsts.USER_LANG);
    }

    public static void setUserLang(String lang) {
        set(SecurityConsts.USER_LANG, lang);
    }

    public static String getUsername() {
        return get(SecurityConsts.USERNAME);
    }

    public static void setUsername(String username) {
        set(SecurityConsts.USERNAME, username);
    }

    public static Long getCompanyId() {
        return Convert.toLong(get(SecurityConsts.COMPANY_ID), 0L);
    }

    public static void setCompanyId(String companyId) {
        set(SecurityConsts.COMPANY_ID, companyId);
    }

    public static Long getOrgId() {
        return Convert.toLong(get(SecurityConsts.ORG_ID), 0L);
    }

    public static void setOrgId(String companyId) {
        set(SecurityConsts.ORG_ID, companyId);
    }

//    public static String getUserKey() {
//        return get(SecurityConsts.USER_KEY);
//    }
//
//    public static void setUserKey(String userKey) {
//        set(SecurityConsts.USER_KEY, userKey);
//    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}

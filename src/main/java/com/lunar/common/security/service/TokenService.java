package com.lunar.common.security.service;

import cn.hutool.core.lang.UUID;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.consts.SecurityConsts;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.utils.IpUtils;
import com.lunar.common.core.utils.JwtUtils;
import com.lunar.common.core.utils.ServletUtils;
import com.lunar.common.redis.utils.RedisUtil;
import com.lunar.common.security.model.Token;
import com.lunar.common.security.utils.SecurityUtils;
import com.google.common.collect.Maps;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.consts.SecurityConsts;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.utils.IpUtils;
import com.lunar.common.core.utils.JwtUtils;
import com.lunar.common.core.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
 */
@Slf4j
@Component
public class TokenService {

    /**
     * 缓存有效期（s），默认3天=72小时
     */
    private final static long EXPIRE_TIME = CacheConsts.EXPIRATION;

    /**
     * 创建令牌
     */
    public Token createToken(LoginUser loginUser) {
        // 生成token
        String token = UUID.fastUUID().toString();
        loginUser.setToken(token);
        loginUser.setIpAddr(IpUtils.getIpAddr(ServletUtils.getRequest()));
        refreshToken(loginUser);

        // Jwt存储信息
        Map<String, Object> claimsMap = Maps.newHashMap();
//        String userKey = loginUser.getUsername() + ":" + token;
        String userKey = getUserKey(loginUser.getCompanyId(), loginUser.getUserId());
        claimsMap.put(SecurityConsts.USER_KEY, userKey);
        claimsMap.put(SecurityConsts.USER_ID, loginUser.getUserId());
        claimsMap.put(SecurityConsts.USERNAME, loginUser.getUsername());
        claimsMap.put(SecurityConsts.COMPANY_ID, loginUser.getCompanyId());
        claimsMap.put(SecurityConsts.ORG_ID, loginUser.getOrgId());

        return Token.builder().accessToken(JwtUtils.createToken(claimsMap)).expiresIn(EXPIRE_TIME).build();
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser() {
        return getLoginUser(ServletUtils.getRequest());
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = SecurityUtils.getToken(request);

        return getLoginUser(token);
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        LoginUser loginUser = null;
        try {
            String userKey = JwtUtils.getUserKey(token);
            loginUser = RedisUtil.get(getTokenKey(userKey));
        } catch (Exception e) {
            log.error("获取用户身份信息失败. token={}", token, e);
        }
        return loginUser;
    }

    /**
     * 刷新令牌有效期，相差不足24小时，自动刷新缓存
     *
     * @param loginUser
     */
    public void refreshTokenIfExpireShort(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime >= CacheConsts.REFRESH_TIME * 1000) {
            refreshToken(loginUser);
        }
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser) {
        if (loginUser != null && StringUtils.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户token
     */
    public void delLoginUser(String token) {
        if (StringUtils.isBlank(token)) {
            return;
        }
        String userKey = JwtUtils.getUserKey(token);
        RedisUtil.del(getTokenKey(userKey));
    }

    /**
     * 删除用户token
     */
    public void delLoginUserById(Long companyId, Long userId) {
        if (companyId == null || userId == null) {
            return;
        }
        String userKey = getUserKey(companyId, userId);
        RedisUtil.del(getTokenKey(userKey));
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + EXPIRE_TIME * 1000);
        // 将loginUser缓存
        String userKey = getUserKey(loginUser.getCompanyId(), loginUser.getUserId());
        RedisUtil.set(getTokenKey(userKey), loginUser, EXPIRE_TIME, TimeUnit.SECONDS);
    }

    /**
     * userKey
     */
    private static String getUserKey(Long companyId, Long userId) {
        return companyId + ":" + userId;
    }

    /**
     * dct:tokens:{companyId}:{userId}
     */
    private String getTokenKey(String userKey) {
        return CacheConsts.LOGIN_TOKEN_KEY + userKey;
    }

}
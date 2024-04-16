package com.lunar.system.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.UUID;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.utils.StringUtils;
import com.lunar.common.redis.utils.RedisUtil;
import com.lunar.system.response.CaptchaResp;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.utils.StringUtils;
import com.lunar.system.response.CaptchaResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 图形验证码
 *
 * @author szx
 * @date 2023/05/22 16:17
 */
@Slf4j
@Service
public class CaptchaService {

    /**
     * 生成验证码
     *
     * @return
     */
    public CaptchaResp generate() {
        String uuid = UUID.randomUUID().toString(true);

        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(96, 32);

        String img = lineCaptcha.getImageBase64Data();

        RedisUtil.set(redisKey(uuid), lineCaptcha.getCode(), CacheConsts.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);

        CaptchaResp resp = CaptchaResp.builder()
            .uuid(uuid)
            .img(img)
            .build();
        return resp;
    }

    /**
     * 校验验证码
     *
     * @param uuid
     * @param code
     */
    public void checkVerifyCode(String uuid, String code) {
        if (StringUtils.isBlank(code)) {
            throw new ServiceException(ApiCode.CAPTCHA_MISSING);
        }
        if (StringUtils.isBlank(uuid)) {
            throw new ServiceException(ApiCode.CAPTCHA_ERROR);
        }

        String key = redisKey(uuid);
        String value = RedisUtil.get(key);

        if (!code.equalsIgnoreCase(value)) {
            throw new ServiceException(ApiCode.CAPTCHA_ERROR);
        }

        RedisUtil.del(key);
    }

    private String redisKey(String uuid) {
        return String.format(CacheConsts.CAPTCHA_KEY, uuid);
    }

}

package com.lunar.system.service;

import com.lunar.common.core.code.ThirdCode;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.enums.EnvType;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.utils.CheckUtil;
import com.lunar.common.redis.utils.RedisUtil;
import com.google.common.collect.Maps;
import com.lunar.common.core.code.ThirdCode;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.enums.EnvType;
import com.lunar.common.core.utils.CheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证码
 *
 * @author szx
 * @date 2019/07/18 19:07
 */
@Slf4j
@Service
public class VerifyService {

    @Value("${spring.profiles.active:prod}")
    private String env;

    /** 验证码长度 6位 */
    private final static int VERIFY_CODE_LEN = 6;
    /** 验证码超时时间 15分钟 */
    private final static int VERIFY_CODE_MIN_TIMEOUT = 15;
    /** 最大重试次数 */
    private final static int MAX_RETRY_COUNT = 3;
    /** 验证码发送有效期（60秒内只能发送一次） */
    private final static long VERIFY_CODE_PERIOD = 60 * 1000L;

    /** hash field 验证码 */
    private final static String KEY_CODE = "code";
    /** hash field 重试次数 */
    private final static String KEY_COUNT = "count";
    /** hash field key生成的时间戳 */
    private final static String KEY_TIMESTAMP = "timestamp";

    /**
     * 生成并发送验证码
     * <p>
     * 1. 有效期15分钟 2. 6位纯数字 3. 60秒内只能发送一次 4. 验证码最多使用3次，不论匹配与否都销毁
     *
     * @param mobile
     * @return
     */
    public String generateAndSendVerifyCode(String mobile) {
        log.info("生成并发送验证码. mobile={}", mobile);

        if (!CheckUtil.isMobile(mobile)) {
            throw new ServiceException(ThirdCode.INVALID_MOBILE);
        }

        String key = getKey(mobile);
        Map<String, Object> hashMap = RedisUtil.hgetall(key);
        // redis中不存在手机号码对应的key，生成验证码
        if (MapUtils.isEmpty(hashMap)) {
            return generateVerifyCode(mobile, key);
        }

        //redis中存在key，校验是否在60秒内。是则报错，否则生成新的覆盖
        long timestamp = (long) hashMap.get(KEY_TIMESTAMP);
        if (System.currentTimeMillis() - timestamp < VERIFY_CODE_PERIOD) {
            log.info(ThirdCode.VERIFY_CODE_EXIST.getMessage());
            throw new ServiceException(ThirdCode.VERIFY_CODE_EXIST);
        } else {
            return generateVerifyCode(mobile, key);
        }
    }

    /**
     * 校验验证码且计数
     *
     * @param mobile
     * @param code
     */
    public void checkVerifyCode(String mobile, String code) {
        log.info("校验验证码且计数. mobile={}, code={}", mobile, code);
        checkVerifyCode(mobile, code, true);
    }

    /**
     * 校验验证码且不计数
     *
     * @param mobile
     * @param code
     */
    public void checkVerifyCodeNotCounting(String mobile, String code) {
        log.info("校验验证码且不计数. mobile={}, code={}", mobile, code);
        checkVerifyCode(mobile, code, false);
    }

    /**
     * @param mobile
     * @param code
     * @param counting 是否计数
     */
    private void checkVerifyCode(String mobile, String code, boolean counting) {
        log.info("校验验证码. mobile={}, code={}, counting={}", mobile, code, counting);

        if (!CheckUtil.isMobile(mobile)) {
            throw new ServiceException(ThirdCode.INVALID_MOBILE);
        }

        String key = getKey(mobile);
        Map<String, Object> hashMap = RedisUtil.hgetall(key);
        //key不存在，校验失败
        if (MapUtils.isEmpty(hashMap)) {
            log.error(ThirdCode.INVALID_VERIFY_CODE.getMessage());
            throw new ServiceException(ThirdCode.INVALID_VERIFY_CODE);
        }

        //key存在，但匹配次数超过3次。不做操作，等待5分钟后验证码失效
        int count = (int) hashMap.get(KEY_COUNT);
        if (count > MAX_RETRY_COUNT) {
            log.error(ThirdCode.VERIFY_CODE_FAILED_TOO_MANY.getMessage());
            throw new ServiceException(ThirdCode.VERIFY_CODE_FAILED_TOO_MANY);
        }

        //count次数+1
        if (counting) {
            RedisUtil.hincr(key, KEY_COUNT);
        }

        //开始匹配
        String redisCode = (String) hashMap.get(KEY_CODE);
        if (!code.equals(redisCode)) {
            log.error(ThirdCode.VERIFY_CODE_ERROR.getMessage());
            throw new ServiceException(ThirdCode.VERIFY_CODE_ERROR);
        }

        // 匹配成功，删除缓存
        RedisUtil.del(key);

        log.info("验证码校验通过");
    }

    /**
     * 拼接key
     *
     * @param mobile
     * @return
     */
    private String getKey(String mobile) {
        return String.format(CacheConsts.REDIS_VFCODE, mobile);
    }

    /**
     * 生成并保存发送验证码
     *
     * @param mobile
     * @param key
     * @return
     */
    private String generateVerifyCode(String mobile, String key) {
        String code = RandomStringUtils.random(VERIFY_CODE_LEN, false, true);
        // 非生产环境，code固定
        if (!EnvType.PROD.getActive().equals(env)) {
            code = "123456";
        }

        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put(KEY_CODE, code);
        hashMap.put(KEY_COUNT, 1);
        hashMap.put(KEY_TIMESTAMP, System.currentTimeMillis());
        RedisUtil.hmset(key, hashMap, VERIFY_CODE_MIN_TIMEOUT, TimeUnit.MINUTES);

        //非生产环境，不调用短信邮件中台，直接返回
        if (!EnvType.PROD.getActive().equals(env)) {
            return code;
        }

        //调用接口发送短信
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("code", code);
        try {
            // TODO: ye 2023/05/22 实际发送验证码短信
            boolean successFlag = true;
//            SendSmsResp sendSmsResp = sendSms(mobile, code);

            if (successFlag) {
                log.info("短信发送成功. mobile[{}], code[{}]", mobile, code);
                return code;
            } else {
                //删除key
                RedisUtil.del(key);
                log.error("发送短信验证码异常. msg={}", "msg");
                throw new ServiceException(ThirdCode.SMS_SEND_ERROR);
            }
        } catch (Exception e) {
            //删除key
            RedisUtil.del(key);
            log.error("发送短信验证码异常. msg={}", e.getMessage());
            throw new ServiceException(ThirdCode.SMS_SEND_ERROR);
        }
    }
}

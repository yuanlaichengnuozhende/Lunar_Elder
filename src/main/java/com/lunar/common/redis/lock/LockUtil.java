package com.lunar.common.redis.lock;

import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.domain.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author szx
 * @date 2022/01/19 10:33
 */
@Slf4j
public class LockUtil {

    public static ApiResult lock(RedissonClient redissonClient, String label, LockBuilder lockBuilder) {
        RLock lock = redissonClient.getFairLock(CacheConsts.REDIS_LOCK_PRE + label);
        boolean locked = false;

        ApiResult apiResult = ApiResult.ok();

        try {
            //尝试获取锁。最多等待2s，若获取成功，最多持有5s
            locked = lock.tryLock(2L, 5L, TimeUnit.SECONDS);
            log.info("[{}]locked={}", label, locked);

            if (locked) {
                // 获取到锁，执行代码
                apiResult = lockBuilder.biz();
            }
        } catch (ServiceException e) {
            log.error("[{}]服务数据处理异常.", label, e);
            apiResult = ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("[{}]数据处理异常.", label, e);
            apiResult = ApiResult.error(StringUtils.isBlank(e.getMessage()) ? e.toString() : e.getMessage());
        } finally {
            //解锁
            log.info("[{}]unlock", label);
            if (locked) {
                lock.unlock();
            }
        }

        return apiResult;
    }

}

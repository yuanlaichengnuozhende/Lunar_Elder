package com.lunar.common.redis.lock;

import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.ApiResult;

/**
 * @author szx
 * @date 2022/01/19 10:21
 */
@FunctionalInterface
public interface LockBuilder {

    ApiResult biz();

}

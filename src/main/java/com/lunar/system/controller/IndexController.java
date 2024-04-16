package com.lunar.system.controller;

import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.localcache.utils.LocalCacheUtil;
import com.lunar.system.entity.DictEnum;
import com.lunar.common.core.domain.ApiResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 监控探活服务
 **/
@RestController
public class IndexController {

    @Autowired
    MeterRegistry registry;

    private Counter counterCore;

    @PostConstruct
    private void init() {
        counterCore = registry.counter("dct-system", "method", "IndexController.core");
    }

    @GetMapping("/core")
    public Object coreUrl() {
        try {
            counterCore.increment();
        } catch (Exception e) {
            return e;
        }
        return ApiResult.ok(counterCore.count() + " coreUrl Monitor by Prometheus.", "调用成功");
    }

    @GetMapping("/health")
    public String health() {
        return "up";
    }

    @GetMapping("/cache/{key}")
    public ApiResult<List<DictEnum>> cache(@PathVariable String key) {
        List<DictEnum> list = LocalCacheUtil.getDictEnumList(key);
        return ApiResult.ok(list);
    }

    @GetMapping("/flush")
    public ApiResult flush() {
        LocalCacheUtil.flush();
        return ApiResult.ok();
    }

}
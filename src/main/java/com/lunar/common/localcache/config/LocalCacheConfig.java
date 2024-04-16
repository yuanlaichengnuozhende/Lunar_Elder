package com.lunar.common.localcache.config;

import com.lunar.common.localcache.utils.LocalCacheUtil;
import com.lunar.system.service.DictDataService;
import com.lunar.system.service.DictEnumService;
import com.lunar.common.localcache.utils.LocalCacheUtil;
import com.lunar.system.service.DictDataService;
import com.lunar.system.service.DictEnumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalCacheConfig {

    @Autowired
    private DictEnumService dictEnumService;
    @Autowired
    private DictDataService dictDataService;

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Bean
    public LocalCacheUtil localCacheUtil() {
        LocalCacheUtil.init(dictEnumService, dictDataService);
        return new LocalCacheUtil();
    }

}

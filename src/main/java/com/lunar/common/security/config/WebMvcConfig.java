package com.lunar.common.security.config;

import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.security.convertor.BaseEnumConverterFactory;
import com.lunar.common.security.convertor.LongDateConverter;
import com.lunar.common.security.filter.LoggerFilter;
import com.lunar.common.security.interceptor.HeaderInterceptor;
import com.lunar.common.security.resolver.EmptyStringArgumentResolver;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;

import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.security.convertor.BaseEnumConverterFactory;
import com.lunar.common.security.convertor.LongDateConverter;
import com.lunar.common.security.interceptor.HeaderInterceptor;
import com.lunar.common.security.resolver.EmptyStringArgumentResolver;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@Configuration
@ServletComponentScan(basePackageClasses = {LoggerFilter.class})
@EnableAsync
public class WebMvcConfig implements WebMvcConfigurer {

    /** 不需要拦截地址 */
    public static final String[] EXCLUDE_URLS = {
        "/login",
        "/logout",
        "/mobile/vfcode",
        "/health",
        "/captcha",
    };

//    @Autowired
//    private AllowOriginInterceptor allowOriginInterceptor;

    @Resource
    @Lazy
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 自定义请求头拦截器
        HeaderInterceptor headerInterceptor = new HeaderInterceptor();

        registry.addInterceptor(headerInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(EXCLUDE_URLS)
            .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**", "/csrf",
                "/doc.html/**")
            .order(-10);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new BaseEnumConverterFactory());
        registry.addConverter(new LongDateConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new EmptyStringArgumentResolver());
    }

//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//        argumentResolvers.add(new RenamingRequestParamMethodArgumentResolver(true));
//    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        HttpMessageConverter<?> converter = converters.stream()
            .filter(e -> e instanceof MappingJackson2HttpMessageConverter)
            .findFirst()
            .orElse(null);
        if (converter != null) {
            MappingJackson2HttpMessageConverter jackson = (MappingJackson2HttpMessageConverter) converter;
            jackson.setObjectMapper(JsonHelper.MAPPER);
        }
    }

    /**
     * 配置线程池
     */
    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(1000);
        taskExecutor.setKeepAliveSeconds(200);
        taskExecutor.setThreadNamePrefix(Consts.APP_NAME + "-thread-pool-");
        // 线程池对拒绝任务（无线程可用）的处理策略，目前只支持AbortPolicy、CallerRunsPolicy；默认为后者
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        // 处理callable超时
        configurer.setDefaultTimeout(60 * 1000);
        // 使用threadPoolTaskExecutor bean
        configurer.setTaskExecutor(threadPoolTaskExecutor);
        configurer.registerCallableInterceptors(new TimeoutCallableProcessingInterceptor());
    }

}

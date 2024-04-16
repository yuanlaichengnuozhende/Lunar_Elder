package com.lunar.common.core.context;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author szx
 */
@Slf4j
public class HandlerContext {

    private final String handlerPackage;
    private final Class<? extends Annotation> annotation;

    private final Map<Enum<?>, Class<?>> handlerMap = Maps.newHashMap();

    public HandlerContext(String handlerPackage, Class<? extends Annotation> annotation) {
        this.handlerPackage = handlerPackage;
        this.annotation = annotation;
    }

    @PostConstruct
    public void init() {
        ClassScanner.scan(handlerPackage, annotation).forEach(clazz -> {
            // 获取注解中的类型值
            Annotation annotation = clazz.getAnnotation(this.annotation);

            try {
                Method method = annotation.annotationType().getMethod("provider");
                method.setAccessible(true);
                Enum<?> type = (Enum<?>) method.invoke(annotation);

                handlerMap.put(type, clazz);
            } catch (Exception e) {
                log.error("handler初始化失败. clazz={}", clazz, e);
            }
        });
    }

    public <T> T getInstance(Enum<?> type) {
        Class<?> clazz = handlerMap.get(type);
        if (clazz == null) {
            throw new IllegalArgumentException("not found handler for type: " + type);
        }
        return ApplicationContextUtil.getBean(clazz);
    }

}
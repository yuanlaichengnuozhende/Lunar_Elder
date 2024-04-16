package com.lunar.common.core.context;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 多provider
 *
 * @author szx
 */
@Slf4j
public class MultiProviderHandlerContext {

    private final String handlerPackage;
    private final Class<? extends Annotation> annotation;
    private final int providerNum;

    private final Map<String, Class<?>> handlerMap = Maps.newHashMap();

    public MultiProviderHandlerContext(String handlerPackage, Class<? extends Annotation> annotation, int providerNum) {
        this.handlerPackage = handlerPackage;
        this.annotation = annotation;
        this.providerNum = providerNum;
    }

    @PostConstruct
    public void init() {
        ClassScanner.scan(handlerPackage, annotation).forEach(clazz -> {
            // 获取注解中的类型值
            Annotation annotation = clazz.getAnnotation(this.annotation);

            try {
                List<String> list = Lists.newArrayListWithCapacity(providerNum);
                for (int i = 0; i < providerNum; i++) {
                    Enum<?> providerEnum = getEnum(annotation, i + 1);
                    list.add(providerEnum.name());
                }

                String key = Joiner.on("-").join(list);

                handlerMap.put(key, clazz);
            } catch (Exception e) {
                log.error("handler初始化失败. clazz={}", clazz, e);
            }
        });
    }

    private Enum<?> getEnum(Annotation annotation, int num) throws Exception {
        String methodName = "provider" + num;
        Method method = annotation.annotationType().getMethod(methodName);
        method.setAccessible(true);
        return (Enum<?>) method.invoke(annotation);
    }

    public String getKey(Enum<?>... enums) {
        List<String> nameList = Arrays.stream(enums).map(Enum::name).collect(Collectors.toList());
        return Joiner.on("-").join(nameList);
    }

    public <T> T getInstance(Enum<?>... enums) {
        return getInstance(getKey(enums));
    }

    public <T> T getInstance(String type) {
        Class<?> clazz = handlerMap.get(type);
        if (clazz == null) {
            throw new IllegalArgumentException("not found handler for type: " + type);
        }
        return ApplicationContextUtil.getBean(clazz);
    }

}
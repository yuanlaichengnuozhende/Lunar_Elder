package com.lunar.common.security.convertor;

import com.lunar.common.core.enums.BaseEnum;
import com.lunar.common.core.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * get请求，枚举转换
 *
 * @author szx
 */
public class BaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {

    private static final Map<Class, Converter> converterMap = new HashMap<>();

    @Override
    @SuppressWarnings("all")
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        Converter converter = converterMap.get(targetType);
        if (converter != null) {
            return converter;
        }
        converter = new StringToValueNameEnumConverter<>(targetType);
        converterMap.put(targetType, converter);
        return converter;
    }

    @SuppressWarnings("all")
    private static class StringToValueNameEnumConverter<T extends BaseEnum> implements Converter<String, T> {

        private Class<T> targetType;

        public StringToValueNameEnumConverter(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        public T convert(String source) {
            int value = Integer.parseInt(source);
            T result = (T) ReflectionUtils.invokeMethod(
                ReflectionUtils.findMethod(targetType, "get", new Class[]{Integer.class}), null, value);
            return result;
        }
    }
}

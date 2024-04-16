package com.lunar.common.core.utils;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * List转Map
 *
 * @author szx
 * @date 2018/09/02 17:47
 */
@Slf4j
public final class ConvertUtil {

    /**
     * List转Map
     *
     * @param list
     * @param keyMapper
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> Map<K, T> list2Map(List<T> list, Function<? super T, ? extends K> keyMapper) {
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }

        Map<K, T> collect = list.stream().collect(Collectors.toMap(keyMapper, a -> a, (k1, k2) -> k1));

        return collect;
    }

    /**
     * List转Map，key为field，值为T
     *
     * @param list
     * @param field
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> Map<K, T> list2Map(List<T> list, String field) {
        Map<K, T> map = Maps.newHashMap();

        if (CollectionUtils.isEmpty(list)) {
            return map;
        }

        list.forEach(t -> {
            //T中是否有key属性
            Field f = null;
            try {
                f = t.getClass().getDeclaredField(field);
            } catch (NoSuchFieldException e) {
                log.error("list转换为map出错，field不存在。list: {}, field：{}", t.getClass().getSimpleName(), field);
            }

            f.setAccessible(true);
            try {
                map.put((K) f.get(t), t);
            } catch (IllegalAccessException e) {
                log.error("list转换为map出错。list: {}, field：{}", t.getClass().getSimpleName(), field);
            }

        });

        return map;
    }

    /**
     * List转Map，key为id
     *
     * @param list
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> Map<K, T> list2MapById(List<T> list) {
        return list2Map(list, "id");
    }

}

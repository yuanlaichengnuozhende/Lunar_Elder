package com.lunar.common.core.utils;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.enums.BaseEnum;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.response.EnumResp;
import com.google.common.collect.Lists;
import com.lunar.common.core.response.EnumResp;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ye
 * @date 2018/08/30 23:39
 */
public class EnumUtil {

    /**
     * 根据class和code获取enum值
     *
     * @param enumClass
     * @param code
     * @param <E>
     * @return
     */
    public static <E extends BaseEnum> E codeOf(Class<E> enumClass, Integer code) {
        if (code == null) {
            return null;
        }

        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据枚举名获取枚举，忽略大小写
     *
     * @param enumClass
     * @param code
     * @param <E>
     * @return
     */
    public static <E extends Enum<E>> E valueOfIgnoreCase(Class<E> enumClass, String code) {
        return valueOf(enumClass, code, true);
    }

    /**
     * 根据枚举名获取枚举，区分大小写
     *
     * @param enumClass
     * @param code
     * @param <E>
     * @return
     */
    public static <E extends Enum<E>> E valueOf(Class<E> enumClass, String code) {
        return valueOf(enumClass, code, false);
    }

    /**
     * 根据枚举名获取枚举
     *
     * @param enumClass
     * @param value
     * @param ignoreCase
     * @param <E>
     * @return
     */
    private static <E extends Enum<E>> E valueOf(Class<E> enumClass, String value, boolean ignoreCase) {
        if (value == null) {
            return null;
        }

        if (ignoreCase) {
            value = value.toUpperCase();
        }

        try {
            E e = Enum.valueOf(enumClass, value);
            return e;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 匹配任意
     *
     * @param enumObj
     * @param code
     * @param <E>
     * @return
     */
    public static <E extends BaseEnum> boolean matchAny(E enumObj, E... code) {
        if (enumObj == null) {
            return false;
        }

        for (E e : code) {
            if (e.equals(enumObj)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 匹配任意
     *
     * @param enumObj
     * @param codeList
     * @param <E>
     * @return
     */
    public static <E extends BaseEnum> boolean matchAny(E enumObj, List<E> codeList) {
        if (enumObj == null) {
            return false;
        }

        for (E e : codeList) {
            if (e.equals(enumObj)) {
                return true;
            }
        }

        return false;
    }

    /**
     * code,name列表
     *
     * @param enumClass
     * @return
     */
    public static List<EnumResp> getEnumsResp(Class<? extends BaseEnum> enumClass) {
        try {
            BaseEnum[] values = (BaseEnum[])
                ReflectionUtils.invokeMethod(enumClass.getMethod("values"), null);
            return Stream.of(values).map(EnumResp::valueOf).collect(Collectors.toList());
        } catch (NoSuchMethodException e) {
            throw new ServiceException(ApiCode.GET_ENUM_ERROR);
        }
    }

    /**
     * enumList 转 EnumRespList
     *
     * @param enumList
     * @return
     */
    public static List<EnumResp> getEnumResp(List<? extends BaseEnum> enumList) {
        if (CollectionUtils.isEmpty(enumList)) {
            return Lists.newArrayList();
        }

        return enumList.stream()
            .map(x -> EnumResp.builder().code(x.getCode()).name(x.getName()).build())
            .collect(Collectors.toList());
    }

}

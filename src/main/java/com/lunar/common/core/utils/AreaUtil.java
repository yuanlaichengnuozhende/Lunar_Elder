package com.lunar.common.core.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * 地址库工具
 */
public class AreaUtil {

    /**
     * 省市区-三级code
     *
     * @param code
     * @return
     */
    public static Set<Integer> getCodeSet(int code) {
        return Sets.newHashSet(getProvinceCode(code), getCityCode(code), code);
    }

    /**
     * 省市区-三级code
     *
     * @param code
     * @return
     */
    public static List<Integer> getCodeList(int code) {
        if (NumUtil.isNullOrZero(code)) {
            return Lists.newArrayList();
        }

        return Lists.newArrayList(getProvinceCode(code), getCityCode(code), code);
    }

    public static int getProvinceCode(int code) {
        return code / 10000 * 10000;
    }

    public static int getCityCode(int code) {
        return code / 100 * 100;
    }

}

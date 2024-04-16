package com.lunar.common.localcache.utils;

import com.lunar.common.core.consts.DictConsts;
import com.lunar.system.entity.DictEnum;
import com.lunar.common.core.consts.DictConsts;
import com.lunar.system.entity.DictEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author szx
 * @date 2023/02/23 11:48
 */
@Slf4j
public class UnitUtil {

    /**
     * 二氧化碳当量单位。eg：1 -> tCO₂e
     *
     * @param value
     * @return
     */
    public static String formatCO2E(String value) {
        try {
            return LocalCacheUtil.getEnumLabelByDictValue(DictConsts.CO2E_UNIT_Z, value);
        } catch (Exception e) {
            log.error("二氧化碳当量单位获取失败, value={}", value, e);
        }

        return "";
    }

    /**
     * 分母单位查询。eg: 1,2 -> kg
     *
     * @param value 1 or 1,2
     * @return eg: kg
     */
    public static String formatFactUnitM(String value) {
        String result = "";
        if (StringUtils.isBlank(value)) {
            return result;
        }

        try {
            String[] split = value.split(",");
            String unit;
            if (split.length > 1) {
                unit = split[1].trim();
            } else {
                unit = split[0].trim();
            }

            return LocalCacheUtil.getEnumLabelByDictValue(DictConsts.FACTOR_UNIT_M, unit);
        } catch (Exception e) {
            log.error("分母单位获取失败, value={}", value, e);
        }

        return result;
    }

    /**
     * 分母单位解析。eg: kg -> 1,2
     *
     * @param label kg
     * @return eg: 1,2
     */
    public static String parseFactUnitM(String label) {
        String result = "";
        if (StringUtils.isBlank(label)) {
            return result;
        }

        try {
            DictEnum dictEnum = LocalCacheUtil.getEnumByDictLabel(DictConsts.FACTOR_UNIT_M, label.trim());
            return dictEnum.getSourceType() + "," + dictEnum.getDictValue();
        } catch (Exception e) {
            log.error("分母单位解析失败, label={}", label, e);
        }

        return result;
    }

}

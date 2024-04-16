package com.lunar.common.core.enums;

import cn.hutool.core.map.CaseInsensitiveMap;
import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 温室气体类型
 *
 * @author szx
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum GasType implements BaseEnum {

    CO2(1, "二氧化碳（CO₂）", "二氧化碳（CO₂）"),
    CH4(2, "甲烷（CH₄）", "甲烷（CH₄）"),
    N2O(3, "氧化亚氮（N₂O）", "氧化亚氮（N₂O）"),
    HFCs(4, "氢氟碳化物（HFCs）", "氢氟碳化物（HFCs）"),
    PFCs(5, "全氟化碳（PFCs）", "全氟化碳（PFCs）"),
    SF6(6, "六氟化硫（SF₆）", "六氟化硫（SF₆）"),
    NF3(7, "三氟化氮（NF₃）", "三氟化氮（NF₃）"),
    CO2E(8, "二氧化碳当量（CO₂e）", "混合气体"),

    ;

    public static void main(String[] args) {
        StringJoiner joiner = new StringJoiner("；");

        for (BaseEnum value : values()) {
            joiner.add(value.getCode() + " " + value.getName());
        }
        System.out.println("温室气体类型。" + joiner);
    }

    private static Map<Integer, GasType> CODE_MAP = Maps.newHashMap();
    /** 忽略大小写 */
    private static CaseInsensitiveMap<String, GasType> NAME_MAP = new CaseInsensitiveMap<>();

    static {
        for (GasType p : GasType.values()) {
            CODE_MAP.put(p.getCode(), p);
            NAME_MAP.put(p.getName(), p);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;
    /** 在报告中的名称 */
    private String reportName;

    GasType(int code, String name, String reportName) {
        this.code = code;
        this.name = name;
        this.reportName = reportName;
    }

    public static GasType get(Integer code) {
        return CODE_MAP.get(code);
    }

    public static GasType getByName(String name) {
        return NAME_MAP.get(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getReportName() {
        return reportName;
    }

    public static String[] getNameArray() {
        return Arrays.stream(GasType.values()).map(GasType::getName).toArray(String[]::new);
    }

    public static String[] getReportNameArray() {
        return Arrays.stream(GasType.values()).map(GasType::getReportName).toArray(String[]::new);
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(code);
    }

}
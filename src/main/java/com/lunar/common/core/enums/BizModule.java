package com.lunar.common.core.enums;

import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;

import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 功能模块
 *
 * @author szx
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum BizModule implements BaseEnum {

//    SYSTEM(1, "系统管理"),
    FACTOR(2, "排放因子库"),
    FOOTPRINT(3, "产品碳足迹"),
    COMPUTATION(4, "企业碳核算"),

    ;

    public static void main(String[] args) {
        StringJoiner joiner = new StringJoiner("；");

        for (BaseEnum value : values()) {
            joiner.add(value.getCode() + " " + value.getName());
        }
        System.out.println("功能模块。" + joiner);
    }

    private static Map<Integer, BizModule> CODE_MAP = Maps.newHashMap();

    static {
        for (BizModule p : BizModule.values()) {
            CODE_MAP.put(p.getCode(), p);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;

    BizModule(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BizModule get(Integer code) {
        return CODE_MAP.get(code);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(code);
    }

}
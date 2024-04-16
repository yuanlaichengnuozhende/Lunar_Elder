package com.lunar.system.enums;

import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;
import com.lunar.common.core.enums.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.lunar.common.core.convert.deserializer.BaseEnumJsonDeserializer;
import com.lunar.common.core.convert.serializer.BaseEnumJsonSerializer;
import com.lunar.common.core.enums.BaseEnum;

import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 资讯状态
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum InfoStatus implements BaseEnum {

    // 待发布、已发布、已下架

    WAIT_PUBLISH(0, "待发布"),
    PUBLISHED(1, "已发布"),
    OUT(2, "已下架"),

    ;

    public static void main(String[] args) {
        StringJoiner joiner = new StringJoiner("；");
        for (BaseEnum value : values()) {
            joiner.add(value.getCode() + " " + value.getName());
        }
        System.out.println("资讯状态。" + joiner);
    }

    private static Map<Integer, InfoStatus> CODE_MAP = Maps.newHashMap();

    static {
        for (InfoStatus element : InfoStatus.values()) {
            CODE_MAP.put(element.getCode(), element);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;

    InfoStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static InfoStatus get(Integer code) {
        return CODE_MAP.get(code);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(code);
    }

}

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

/**
 * 角色类型
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum RoleType implements BaseEnum {

    PRESET(0, "预置角色"),
    CUSTOM(1, "自定义角色"),

    ;

    private static Map<Integer, RoleType> CODE_MAP = Maps.newHashMap();

    static {
        for (RoleType element : RoleType.values()) {
            CODE_MAP.put(element.getCode(), element);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;

    RoleType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RoleType get(Integer code) {
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

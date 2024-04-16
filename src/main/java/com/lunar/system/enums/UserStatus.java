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
 * 用户状态
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum UserStatus implements BaseEnum {

    ON(0, "启用"),
    OFF(1, "禁用"),

    ;

    private static Map<Integer, UserStatus> CODE_MAP = Maps.newHashMap();

    static {
        for (UserStatus element : UserStatus.values()) {
            CODE_MAP.put(element.getCode(), element);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;

    UserStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserStatus get(Integer code) {
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

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

/**
 * 证件类型
 *
 * @author szx
 * @date 2022/01/18 15:59
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum IdType implements BaseEnum {

    ID_CARD(1, "身份证"),
    PASSPORT(2, "护照"),

    ;

    private static Map<Integer, IdType> CODE_MAP = Maps.newHashMap();

    static {
        for (IdType p : IdType.values()) {
            CODE_MAP.put(p.getCode(), p);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;

    IdType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static IdType get(Integer code) {
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

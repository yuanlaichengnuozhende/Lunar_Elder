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
 * 语言
 *
 * @author szx
 * @date 2019/07/18 11:45
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum LangType implements BaseEnum {

    ZH(1, "zh"),
    EN(2, "en"),

    ;

    private static Map<Integer, LangType> CODE_MAP = Maps.newHashMap();
    private static Map<String, LangType> NAME_MAP = Maps.newHashMap();

    static {
        for (LangType p : LangType.values()) {
            CODE_MAP.put(p.getCode(), p);
            NAME_MAP.put(p.getName(), p);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
        NAME_MAP = Collections.unmodifiableMap(NAME_MAP);
    }

    private int code;
    private String name;

    LangType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static LangType get(Integer code) {
        return CODE_MAP.get(code);
    }

    public static LangType getByName(String name) {
        return NAME_MAP.get(name);
    }

    /**
     * 默认中文
     */
    public static LangType getByNameDefaultIfNull(String name) {
        LangType langType = NAME_MAP.get(name);
        return langType == null ? ZH : langType;
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
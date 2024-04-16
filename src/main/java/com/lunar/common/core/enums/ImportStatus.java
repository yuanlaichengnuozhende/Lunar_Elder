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
 * 导入状态
 *
 * @author szx
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum ImportStatus implements BaseEnum {

    // 导入状态。0 导入中 1 已导入 -1 导入失败

    IMPORTING(0, "导入中"),
    IMPORT_SUCCESS(1, "已导入"),
    IMPORT_FAILED(-1, "导入失败"),

    ;

    public static void main(String[] args) {
        StringJoiner joiner = new StringJoiner("；");

        for (BaseEnum value : values()) {
            joiner.add(value.getCode() + " " + value.getName());
        }
        System.out.println("导入状态。" + joiner);
    }

    private static Map<Integer, ImportStatus> CODE_MAP = Maps.newHashMap();

    static {
        for (ImportStatus p : ImportStatus.values()) {
            CODE_MAP.put(p.getCode(), p);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;

    ImportStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ImportStatus get(Integer code) {
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
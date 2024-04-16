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
 * 审批内容
 *
 * @author szx
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum AuditType implements BaseEnum {

    COMPUTATION_DATA(1, "企业碳核算排放数据审核"),
//    FOOTPRINT_DATA(2, "产品碳足迹审核"),

    ;

    public static void main(String[] args) {
        StringJoiner joiner = new StringJoiner("；");

        for (BaseEnum value : values()) {
            joiner.add(value.getCode() + " " + value.getName());
        }
        System.out.println("审批内容。" + joiner);
    }

    private static Map<Integer, AuditType> CODE_MAP = Maps.newHashMap();

    static {
        for (AuditType p : AuditType.values()) {
            CODE_MAP.put(p.getCode(), p);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;

    AuditType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AuditType get(Integer code) {
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
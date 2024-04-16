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
 * 组织类型
 *
 * @author szx
 */
@JsonDeserialize(using = BaseEnumJsonDeserializer.class)
@JsonSerialize(using = BaseEnumJsonSerializer.class)
public enum OrgType implements BaseEnum {

    SINGLE(0, "单体组织", 1),
    GROUP(1, "集团", 1),
    BRANCH(2, "子组织", 2),
    DEPT(3, "部门", 3),

    ;

    public static void main(String[] args) {
        StringJoiner joiner = new StringJoiner("；");

        for (BaseEnum value : values()) {
            joiner.add(value.getCode() + " " + value.getName());
        }
        System.out.println("组织类型。" + joiner);
    }


    private static Map<Integer, OrgType> CODE_MAP = Maps.newHashMap();

    static {
        for (OrgType p : OrgType.values()) {
            CODE_MAP.put(p.getCode(), p);
        }
        CODE_MAP = Collections.unmodifiableMap(CODE_MAP);
    }

    private int code;
    private String name;
    /** 组织等级 */
    private int level;

    OrgType(int code, String name, int level) {
        this.code = code;
        this.name = name;
        this.level = level;
    }

    public static OrgType get(Integer code) {
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

    public int getLevel() {
        return level;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(code);
    }

}